document.addEventListener("DOMContentLoaded", function () {
  // By default, show all tasks when the page loads
  showAllTasks();
  showAllProjects();
});

document.addEventListener("DOMContentLoaded", function () {
  fetch("/dashboard/data")
    .then((response) => response.json())
    .then((data) => {
      updateDashboard(data);
    })
    .catch((error) => {
      console.error("Error fetching data:", error);
    });

  function updateDashboard(data) {
    // Update progress value
    const progressElement = document.getElementById("progress-value");
    if (progressElement) {
      const progress = !isNaN(data.progress) ? data.progress : 0;
      progressElement.innerText = `${progress}%`;
    }
    

    // Update remaining tasks value
    const remainingTasksElement = document.getElementById(
      "remaining-tasks-value"
    );
    if (remainingTasksElement) {
      remainingTasksElement.innerText = data.remainingTasks;
    }

    // Update completed tasks value
    const completedTasksElement = document.getElementById(
      "completed-tasks-value"
    );
    if (completedTasksElement) {
      completedTasksElement.innerText = data.completedTasks;
    }

    // Update total tasks value
    const totalTasksElement = document.getElementById("total-tasks-value");
    if (totalTasksElement) {
      totalTasksElement.innerText = data.totalTasks;
    }

    // Create pie chart for progress
    const progressChartCanvas = document.createElement("canvas");
    progressChartCanvas.width = 150;
    progressChartCanvas.height = 150;

    const progressChartContainer = document.getElementById("progress-chart");
    if (progressChartContainer) {
      progressChartContainer.innerHTML = ""; // Clear previous content
      progressChartContainer.appendChild(progressChartCanvas);

      new Chart(progressChartCanvas, {
        type: "pie",
        data: {
          labels: ["Completed", "Remaining"],
          datasets: [
            {
              data: [data.completedTasks, data.remainingTasks],
              backgroundColor: ["#36a2eb", "#ff6384"],
            },
          ],
        },
        options: {
          responsive: true,
        },
      });
    }
  }
});

function showAllTasks() {
  fetch("/dashboard/allTasks")
    .then((response) => response.json())
    .then((data) => displayTasks(data));
}

// Function to fetch and display completed tasks
function showCompletedTasks() {
  fetch("/dashboard/completedTask")
    .then((response) => response.json())
    .then((data) => displayTasks(data));
}

// Function to fetch and display incomplete tasks
function showIncompleteTasks() {
  fetch("/dashboard/inCompletedTask")
    .then((response) => response.json())
    .then((data) => displayTasks(data));
}

// Function to dynamically populate the task container with task items
function displayTasks(tasks) {
  const taskContainer = document.getElementById("taskContainer");
  taskContainer.innerHTML = "";
  tasks.forEach((task) => {
    const taskItem = document.createElement("div");
    taskItem.classList.add("task-item");
    taskItem.textContent = `${task.name}`;
    taskContainer.appendChild(taskItem);
  });
}
// Function to fetch data and populate the project list
fetch("/dashboard/projects")
  .then((response) => response.json())
  .then((data) => {
    const projectList = document.querySelector(".project-list");
    data.forEach((project) => {
      const projectItem = document.createElement("li");
      projectItem.classList.add("project-item");
      projectItem.textContent = `${project.name} - ${project.description}`;
      projectList.appendChild(projectItem);
    });
  });

// Function to fetch data and populate the team members list
fetch("/dashboard/getAllTeamMembers")
  .then((response) => response.json())
  .then((data) => {
    const userList = document.querySelector(".user-list");
    data.forEach((user) => {
      const userItem = document.createElement("li");
      userItem.classList.add("user-item");
      userItem.textContent = `${user.name} - ${user.email}`;
      userList.appendChild(userItem);
    });
  });

function showAllProjects() {
  fetch("/dashboard/projects")
    .then((response) => response.json())
    .then((data) => displayProjects(data));
}

// Function to fetch and display completed projects
function showCompletedProjects() {
  fetch("/dashboard/compeletedProjects")
    .then((response) => response.json())
    .then((data) => displayProjects(data));
}

// Function to fetch and display incomplete projects
function showIncompleteProjects() {
  fetch("/dashboard/inCompleteProject")
    .then((response) => response.json())
    .then((data) => displayProjects(data));
}
const projectTitle = document.getElementById("project-title");

// Function to display projects in the project container
function displayProjects(projects) {
  const projectContainer = document.getElementById("projectContainer");
  projectContainer.innerHTML = ""; // Clear existing content
  projects.forEach((project) => {
    const projectItem = document.createElement("div");
    projectItem.classList.add("project-item");

    // Construct project item HTML
    projectItem.innerHTML = `
            <h3>${project.name}</h3>
            <p>Project Head: ${project.creatorName}</p>
            <p>Due Date: ${project.dueDate}</p>
            <p>Status: ${project.completed ? "Completed" : "Incomplete"}</p>
        `;

    // Add click event listener to project item
    projectItem.addEventListener("click", () => {
      console.log(project.projectId);
      projectTitle.innerHTML = `<h3>of ${project.name}</h3>`;
      showProjectEmployees(project.projectId); // Corrected: Pass project ID as argument
    });

    // Append project item to container
    projectContainer.appendChild(projectItem);
  });
}

// Function to fetch all team members
function fetchAllTeamMembers() {
  fetch("/dashboard/getAllTeamMembers")
    .then((response) => response.json())
    .then((data) => {
      displayTeamMembers(data);
    })
    .catch((error) => {
      console.error("Error fetching team members:", error);
    });
}

// Function to display team members
function displayTeamMembers(members) {
  const memberContainer = document.getElementById("memberContainer");
  memberContainer.innerHTML = ""; // Clear previous content

  members.forEach((member) => {
    const memberDiv = document.createElement("div");
    memberDiv.classList.add("memberBox"); // Add class for styling
    memberDiv.innerHTML = `
            <div class="memberName">${member.name}</div>
            <div class="memberEmail">${member.email}</div>
        `;
    memberContainer.appendChild(memberDiv);
  });
}

// Call fetchAllTeamMembers function when page loads
window.onload = function () {
  fetchAllTeamMembers();
};

function displayProjectMembers(members) {
  const employeeContainer = document.getElementById("employeeContainerInner");
  employeeContainer.innerHTML = ""; // Clear existing content

  members.forEach((member) => {
    const memberItem = document.createElement("div");
    memberItem.classList.add("memberBox");

    // Construct member item HTML
    memberItem.innerHTML = `
            <p>Name: ${member.name}</p>
            <p>Email: ${member.email}</p>
        `;

    // Append member item to container
    employeeContainer.appendChild(memberItem);
  });
  document.getElementById("selection").style.display = "none";
}

function showProjectEmployees(projectId) {
  fetch(`/dashboard/getProjectMembers/${projectId}`)
    .then((response) => response.json())
    .then((data) => displayProjectMembers(data));

  // Show the employee container
  document.getElementById("employeeContainer").style.display = "block";
}

// Function to fetch and display all projects
function showAllProjects() {
  fetch("/dashboard/projects")
    .then((response) => response.json())
    .then((data) => displayProjects(data));

  // Hide the employee container
  //   document.getElementById("selection").style.display = "none";
}

// Function to fetch and display completed projects
function showCompletedProjects() {
  fetch("/dashboard/compeletedProjects")
    .then((response) => response.json())
    .then((data) => displayProjects(data));

  // Hide the employee container
  //   document.getElementById("selection").style.display = "none";
}

// Function to fetch and display incomplete projects
function showIncompleteProjects() {
  fetch("/dashboard/inCompleteProject")
    .then((response) => response.json())
    .then((data) => displayProjects(data));

  // Hide the employee container
  //   document.getElementById("selection").style.display = "none";
}

fetch("/dashboard/allData")
  .then((response) => response.json())
  .then((data) => {
    // Extract data
    console.log(data);
    const remainingProjects = data.remainingProjects;
    const completedTasks = data.completedTasks;
    const totalProjects = data.totalProjects;
    const usersInProjects = data.usersInProjects;
    const remainingTasks = data.remainingTasks;
    const totalTasks = data.totalTasks;
    const taskProgress = data.taskProgress;
    const completedProjects = data.completedProjects;
    const tasksInProject = data.tasksInProjects;

    // Create Pie Chart
    new Chart(document.getElementById("pieChart"), {
      type: "pie",
      data: {
        labels: ["Completed Tasks", "Remaining Tasks"],
        datasets: [
          {
            data: [completedTasks, remainingTasks],
            backgroundColor: ["#36a2eb", "#ff6384"],
          },
        ],
      },
      options: {
        title: {
          display: true,
          text: "Task Progress",
        },
      },
    });

    new Chart(document.getElementById("barChart"), {
      type: "bar",
      data: {
        labels: ["Total Projects", "Completed Projects", "Remaining Projects"],
        datasets: [
          {
            label: "Total Projects",
            data: [totalProjects, 0, 0],
            backgroundColor: "#36a2eb",
          },
          {
            label: "Completed Projects",
            data: [0, completedProjects, 0],
            backgroundColor: "#ff6384",
          },
          {
            label: "Remaining Projects",
            data: [0, 0, remainingProjects],
            backgroundColor: "#ffcd56",
          },
        ],
      },
      options: {
        title: {
          display: true,
          text: "Project Summary",
        },
      },
    });

    // Create Line Chart
    const projectLabels = Object.keys(usersInProjects).map((name) => `${name}`);
    const projectData = Object.values(usersInProjects);
    new Chart(document.getElementById("lineChart"), {
      type: "line",
      data: {
        labels: projectLabels,
        datasets: [
          {
            label: "Users in Projects",
            data: projectData,
            borderColor: "#ff6384",
            fill: false,
          },
        ],
      },
      options: {
        title: {
          display: true,
          text: "Users in Projects",
        },
        scales: {
          yAxes: [
            {
              ticks: {
                min: 0,
              },
            },
          ],
        },
      },
    });
    const projectLabelsTasks = Object.keys(tasksInProject).map(
      (name) => `${name}`
    );
    const projectDataTasks = Object.values(tasksInProject);
    new Chart(document.getElementById("lineChartTasks"), {
      type: "line",
      data: {
        labels: projectLabelsTasks,
        datasets: [
          {
            label: "Users in Projects",
            data: projectDataTasks,
            borderColor: "#ff6384",
            fill: false,
          },
        ],
      },
      options: {
        title: {
          display: true,
          text: "Users in Projects",
        },
        scales: {
          yAxes: [
            {
              ticks: {
                min: 0,
              },
            },
          ],
        },
      },
    });
  });
