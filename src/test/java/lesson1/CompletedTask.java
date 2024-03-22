package lesson1;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.Test;

import static io.restassured.RestAssured.given;
import static org.junit.Assert.assertEquals;

public class CompletedTask extends ApiTests {

    @Test
    public void testCompleteTask() {
        // Создание новой задачи
        String title = "New Task";
        Response createResponse = given()
                .contentType(ContentType.JSON)
                .body("{\"title\": \"" + title + "\"}")
                .post();
        // Получение идентификатора созданной задачи из тела ответа
        String taskId = createResponse.getBody().jsonPath().getString("id");
        assertEquals(201, createResponse.getStatusCode());
        // Флаг выполнения задачи
        boolean completed = true;
        // Отправка PATCH-запроса для выполнения задачи
        Response response = given()
                .contentType(ContentType.JSON)
                .body("{\"completed\": " + completed + "}")
                .patch("/" + taskId);

        assertEquals(200, response.getStatusCode());
        // Получение информации о задаче после выполнения запроса
        Response updatedTaskResponse = RestAssured.get("/" + taskId);
        assertEquals(200, updatedTaskResponse.getStatusCode());
        // Проверка, что задача действительно выполнена
        boolean taskCompleted = updatedTaskResponse.jsonPath().getBoolean("completed");
        assertEquals(completed, taskCompleted);
    }

    @Test
    public void testInvalidTaskId() {
        // Попытка выполнения задачи с недопустимым идентификатором
        String taskId = "3456789";
        boolean completed = true;

        Response response = given()
                .contentType(ContentType.JSON)
                .body("{\"completed\": " + completed + "}")
                .patch("/" + taskId);
        assertEquals(404, response.getStatusCode());
    }
}
