package proCollab.projectManagement.capstoneProject.controller;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import proCollab.projectManagement.capstoneProject.model.Company;
import proCollab.projectManagement.capstoneProject.model.Project;
import proCollab.projectManagement.capstoneProject.model.Task;
import proCollab.projectManagement.capstoneProject.model.Teams;
import proCollab.projectManagement.capstoneProject.model.User;
import proCollab.projectManagement.capstoneProject.repository.TeamRepository;
import proCollab.projectManagement.capstoneProject.service.ProjectService;
import proCollab.projectManagement.capstoneProject.service.TaskHistoryService;
import proCollab.projectManagement.capstoneProject.service.TaskService;
import proCollab.projectManagement.capstoneProject.service.UserService;

@RestController
@RequestMapping("/projects")
public class ProjectController {

    private final ProjectService projectService;
    private final UserService userService;
    private final TaskHistoryService taskHistoryService;
    private final TaskService taskService;
    @Autowired
    private final TeamRepository teamRepository;

    public ProjectController(ProjectService projectService, UserService userService,
            TaskHistoryService taskHistoryService, TaskService taskService, TeamRepository teamRepository) {
        this.projectService = projectService;
        this.userService = userService;
        this.taskHistoryService = taskHistoryService;
        this.taskService = taskService;
        this.teamRepository = teamRepository;
    }

    @PostMapping("/createProject")
    public ResponseEntity<Map<String, String>> addProject(Principal principal, @RequestBody Project project) {
        String email = principal.getName();
        User user = userService.getUserByEmail(email);
        Company company = user.getCompany();
        project.setCreator(user);
        project.setCompany(company);
        Project savedProject = projectService.createProject(project);
        addEmployeeToProject(savedProject.getId(), user.getId());

        // Create a response map with a success message
        Map<String, String> response = new HashMap<>();
        response.put("message", "Project created successfully");

        // Return ResponseEntity with the response map and HTTP status OK
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{projectId}")
    public ResponseEntity<Void> deleteProject(@PathVariable Long projectId) {
        Project project = projectService.getProjectById(projectId);
        List<Task> task = project.getTasks();
        for (Task t : task) {
            t.setTeam(null);
            t.setProject(null);
            t.setCreatorName(null);
            t.setOwner(null);
            taskService.updateTask(t.getId(), t);
            taskHistoryService.deleteAllTaskHistoryByTask(t);
            taskService.deleteTask(t.getId());
        }

        // taskService.deleteAllTaskFromProject(project);
        teamRepository.deleteAllByProject(project);
        projectService.deleteProject(projectId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{projectId}/employees/{userId}")
    public ResponseEntity<Void> addEmployeeToProject(@PathVariable Long projectId, @PathVariable Long userId) {
        User user = userService.getUserById(userId);
        projectService.addEmployeeToProject(projectId, user);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{projectId}/employees")
    public ResponseEntity<Void> addEmployeesToProject(@PathVariable Long projectId, @RequestBody List<Long> userIds) {
        for (Long userId : userIds) {
            User user = userService.getUserById(userId);
            projectService.addEmployeeToProject(projectId, user);
        }
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{projectId}/employees/{userId}")
    public ResponseEntity<Void> removeEmployeeFromProject(@PathVariable Long projectId, @PathVariable Long userId) {
        User user = userService.getUserById(userId);
        projectService.removeEmployeeFromProject(projectId, user);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{projectId}/employees")
    public ResponseEntity<Void> removeSelectedEmployeesFromProject(@PathVariable Long projectId,
            @RequestBody List<Long> userIds) {
        Project project = projectService.getProjectById(projectId);
        List<Teams> projectTeams = project.getTeams();
        List<Task> projectTask = project.getTasks();
        for (Long userId : userIds) {
            User user = userService.getUserById(userId);
            List<Teams> userTeams = user.getTeams();
            for (Teams t : userTeams) {
                if (projectTeams.contains(t)) {
                    List<User> users = t.getUsers();
                    List<Task> taskList = user.getTasksOwned();
                    for (Task ta : taskList) {
                        if (projectTask.contains(ta)) {
                            ta.setOwner(null);
                            taskService.updateTask(ta.getId(), ta);
                        }
                    }
                    users.remove(user);
                    t.setUsers(users);
                    teamRepository.save(t);
                }
            }
            projectService.removeEmployeeFromProject(projectId, user);
        }
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{projectId}/employees")
    public ResponseEntity<List<User>> getAllProjectEmployees(@PathVariable Long projectId) {
        List<User> employees = projectService.getAllProjectEmployees(projectId);
        return ResponseEntity.ok(employees);
    }

    // Add other endpoints as needed
}
