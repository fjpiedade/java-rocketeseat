package phi.raiz.tech.todolist.task;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import phi.raiz.tech.todolist.utils.Utils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/tasks")
public class TaskController {
    @Autowired
    private ITaskRepository taskRepository;

    @PostMapping("/")
    public ResponseEntity create(@RequestBody TaskModel taskModel, HttpServletRequest request) {
        var idUser = (UUID) request.getAttribute("idUser");
        taskModel.setIdUser(idUser);
        var currentDate = LocalDateTime.now();

        if (currentDate.isAfter(taskModel.getStartAt())) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Start Date should be on the future!");
        }

        if (currentDate.isAfter(taskModel.getEndAt())) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("End Date of the Task, should be on the future!");
        }

        if (taskModel.getStartAt().isAfter(taskModel.getEndAt())) {
            return ResponseEntity.
                    status(HttpStatus.BAD_REQUEST)
                    .body("Start Date should be before than End Date!");
        }

        var taskCreated = this.taskRepository.save(taskModel);
        System.out.println("task created successfully...");
        return ResponseEntity.status(HttpStatus.CREATED).body(taskCreated);
    }

    @GetMapping("/")
    public ResponseEntity<List<TaskModel>> listTaskByUser(HttpServletRequest request) {
        var idUser = (UUID) request.getAttribute("idUser");
        System.out.println("Listing Task by idUser");
        return ResponseEntity.status(HttpStatus.OK).body(taskRepository.findByIdUser(idUser));
    }

    @PutMapping("/{id}")
    public ResponseEntity update(@RequestBody TaskModel taskModel, @PathVariable UUID id) {

        //Utils.copyNonNullProperties(taskModel, task);

        return ResponseEntity.status(HttpStatus.OK).body(taskRepository.save(taskModel));
    }

    @PutMapping("/v2/{id}")
    public ResponseEntity updateOne(@RequestBody TaskModel taskModel, @PathVariable UUID id, HttpServletRequest request) {

        //Utils.copyNonNullProperties(taskModel, task);

        var idUser = request.getAttribute("idUser");

        var task = this.taskRepository.findById(id).orElse(null);

        if(task == null){
            return ResponseEntity.
                    status(HttpStatus.BAD_REQUEST)
                    .body("Task doesn't exist!");

        }

        if(!task.getIdUser().equals(idUser)){
            return ResponseEntity.
                    status(HttpStatus.BAD_REQUEST)
                    .body("Not Authorized to change this Task!");

        }

        Utils.copyNonNullProperties(taskModel,task);

        //taskModel.setIdUser((UUID) idUser);
        //taskModel.setId(id);
        //return this.taskRepository.save(task);
        return ResponseEntity.status(HttpStatus.OK).body(taskRepository.save(task));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity delete(@RequestBody TaskModel taskModel, @PathVariable UUID id) {
        taskRepository.delete(taskModel);
        return ResponseEntity.status(HttpStatus.OK).body("Deleted Task Successfully...");
    }
}
