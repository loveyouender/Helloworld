package com.mp.demo;

public class LeaderRatifyWorker implements Worker {

	private String taskDefName;

	public SampleWorker(String taskDefName) {
        this.taskDefName = taskDefName;
    }

	@Override
	public String getTaskDefName() {
		return taskDefName;
	}

	@Override
	public TaskResult execute(Task task) {
		System.out.printf("Executing %s%n", taskDefName);
		System.out.println("staffName:" + task.getInputData().get("staffName"));
		System.out.println("staffDepartment:" + task.getInputData().get("staffDepartment"));
		TaskResult result = new TaskResult(task);
		result.setStatus(TaskResult.Status.COMPLETED);
		// Register the output of the task
		result.getOutputData().put("outputKey1", "value");
		result.getOutputData().put("oddEven", 1);
		result.getOutputData().put("mod", 4);
		result.getOutputData().put("leaderAgree", "yes");
		result.getOutputData().put("leaderDisagree", "no");
		return result;
	}
}

class ManagerRatifyWorker implements Worker {
	private String taskDefName;

	public SampleWorker2(String taskDefName) {
        this.taskDefName = taskDefName;
    }

	@Override
	public String getTaskDefName() {
		return taskDefName;
	}

	@Override
	public TaskResult execute(Task task) {
		System.out.printf("Executing %s\n", taskDefName);
		System.out.println("managerName:" + task.getInputData().get("managerName"));
		System.out.println("managerDepartment:" + task.getInputData().get("managerDepartment"));
		TaskResult result = new TaskResult(task);
		result.setStatus(TaskResult.Status.COMPLETED);
		// Register the output of the task
		result.getOutputData().put("managerAgree", String.valueOf(task.getInputData().get("managerName")));
		result.getOutputData().put("managerDisagree", String.valueOf(task.getInputData().get("managerDepartment")));

		return result;
	}

}

	// 在main方法中创建工作Worker以及设置需要访问的Conductor Server端api地址，并将流程进入初始化
	public static void main(String[] args) {
		TaskClient taskClient = new TaskClient();
		taskClient.setRootURI("http://localhost:8080/api/"); // Point this to
																// the server
																// API
		int threadCount = 2; // number of threads used to execute workers. To
								// avoid starvation, should be same or more than
								// number of workers
		Worker worker1 = new LeaderRatifyWorker("leaderRatify");
		Worker worker2 = new ManagerRatifyWorker("managerRatify");
		// Create WorkflowTaskCoordinator
		WorkflowTaskCoordinator.Builder builder = new WorkflowTaskCoordinator.Builder();
		WorkflowTaskCoordinator coordinator = builder.withWorkers(worker1, worker2).withThreadCount(threadCount)
				.withTaskClient(taskClient).build();
		// Start for polling and execution of the tasks
		coordinator.init();
	}
}
