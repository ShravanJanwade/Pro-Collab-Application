
package proCollab.projectManagement.capstoneProject.controller;
 
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
 
import org.apache.tomcat.util.threads.TaskThread;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.servletapi.SecurityContextHolderAwareRequestWrapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
 
import proCollab.projectManagement.capstoneProject.model.ChatMessage;
import proCollab.projectManagement.capstoneProject.model.Note;
import proCollab.projectManagement.capstoneProject.model.Project;
import proCollab.projectManagement.capstoneProject.model.Role;
import proCollab.projectManagement.capstoneProject.model.Task;
import proCollab.projectManagement.capstoneProject.model.TaskHistory;
import proCollab.projectManagement.capstoneProject.model.TaskThreads;
import proCollab.projectManagement.capstoneProject.model.Teams;
import proCollab.projectManagement.capstoneProject.model.User;
import proCollab.projectManagement.capstoneProject.repository.ChatMessageRepository;
import proCollab.projectManagement.capstoneProject.repository.NoteRepository;
import proCollab.projectManagement.capstoneProject.repository.ProjectRepository;
import proCollab.projectManagement.capstoneProject.repository.TaskHistoryRepository;
import proCollab.projectManagement.capstoneProject.repository.TaskRepository;
import proCollab.projectManagement.capstoneProject.repository.TaskThreadsRepository;
import proCollab.projectManagement.capstoneProject.repository.TeamRepository;
import proCollab.projectManagement.capstoneProject.repository.UserRepository;
import proCollab.projectManagement.capstoneProject.service.CompanyService;
import proCollab.projectManagement.capstoneProject.service.UserService;
 
@Controller
public class UserController {
 
    private UserService userService;
    private CompanyService companyService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ProjectRepository projectRepository;
    @Autowired
    private ChatMessageRepository chatMessageRepository;
    @Autowired
    private TaskHistoryRepository taskHistoryRepository; // Assuming you have a repository for TaskHistory entity
    @Autowired
    private TaskRepository taskRepository; // Assuming you have a repository for Task entity
 
    @Autowired
    private TeamRepository teamUserRepository; // Assuming you have a repository for TeamUser entity
 
    @Autowired
    private NoteRepository noteRepository;
 
    @Autowired
    private TaskThreadsRepository taskThreadsRepository;
    @Autowired
    public UserController(UserService userService, CompanyService companyService) {
        this.userService = userService;
        this.companyService = companyService;
    }
 
    @GetMapping("/users")
    public String listUsers(Principal principal, Model model, SecurityContextHolderAwareRequestWrapper request) {
        boolean isAdminSigned = request.isUserInRole("ROLE_ADMIN");
        String email = principal.getName();
        User user = userService.getUserByEmail(email);
        List<User> allUsers = companyService.getCompanyUsers(user.getCompany().getId());
        List<User> ourUsers = new ArrayList<>();
        for (User u : allUsers) {
            for (Role role : u.getRoles()) {
                if (!role.getRole().equals("SUPERADMIN")) {
                    ourUsers.add(u);
                }
            }
        }
        model.addAttribute("users", ourUsers);
        model.addAttribute("isAdminSigned", isAdminSigned);
        return "views/users";
    }
 
    @GetMapping("/user/delete/{id}")
    public String deleteUser(@PathVariable Long id,
            @RequestParam(name = "redirect", required = false) String redirectUrl) {
        // Fetch the user by id
        User user = userRepository.findById(id).orElse(null);
       
        if (user != null) {

            List<Note> userNotes=user.getNotesOwned();
            for(Note note:userNotes){
                note.setNote_owner(null);
                noteRepository.delete(note);
            }
            List<TaskThreads> threads=taskThreadsRepository.findByUser(user);
            for(TaskThreads t:threads){
                t.setUser(null);
                taskThreadsRepository.delete(t);
            }
            List<ChatMessage> chatMessages = chatMessageRepository.findBySenderOrRecipient(user, user);
            chatMessageRepository.deleteAll(chatMessages);
            List<TaskHistory> taskHistories = taskHistoryRepository.findByUser(user);
            taskHistoryRepository.deleteAll(taskHistories);
 
            // Remove the user from associated projects
            List<Project> projects = user.getProjects();
            for (Project project : projects) {
                project.getEmployees().remove(user);
                projectRepository.save(project); // Update the project to reflect changes
            }
            List<Teams> teams = user.getTeams();
            for (Teams team : teams) {
                team.getUsers().remove(user);
                teamUserRepository.save(team);
            }
            List<Task> tasksOwnedByUser = taskRepository.findByOwnerOrderByDateDesc(user);
            List<Task> taskCreated=taskRepository.findByCreatedUser(user);
            // List<Task> taskCreated=taskRepository.findByCreatedUser(user);
            // Then remove the owner reference from each task
            for (Task task : tasksOwnedByUser) {
                task.setOwner(null);
                taskRepository.save(task); // Update the task without the owner reference
            }
            for (Task task : taskCreated) {
                task.setOwner(null);
                task.setCreatedUser(null);
                taskRepository.save(task); // Update the task without the owner reference
            }
            List<Project> projectss=projectRepository.findByCreator(user);
            for(Project project:projectss){
                project.setCreator(null);
                projectRepository.save(project);
            }
            // for (Task task : taskCreated) {
            //     task.setOwner(null);
            //     task.setCreatedUser(null);
            //     taskRepository.save(task); // Update the task without the owner reference
            // }
            // List<Project> projectss=projectRepository.findByCreator(user);
            // for(Project project:projectss){
            //     project.setCreator(null);
            //     projectRepository.save(project);
            // }
            // Now delete the user
            userRepository.deleteById(id);
        }
        if (redirectUrl != null) {
            return "redirect:/superAdmin/assignManager";
        }
 
        return "redirect:/users";
    }
 
}
 
