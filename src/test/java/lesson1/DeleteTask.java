package lesson1;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.Test;

import static io.restassured.RestAssured.given;
import static org.junit.Assert.assertEquals;

public class DeleteTask extends ApiTests {
    @Test
    public void testDeleteTask() {
        // Создание новой задачи
        String title = "New Task";
        Response createResponse = given()
                .contentType(ContentType.JSON)
                .body("{\"title\": \"" + title + "\"}")
                .post();
        // Получение идентификатора созданной задачи из тела ответа
        String taskId = createResponse.getBody().jsonPath().getString("id");
        assertEquals(201, createResponse.getStatusCode());
        // Отправка DELETE-запроса для удаления задачи
        Response deleteResponse = RestAssured.delete("/" + taskId);
        assertEquals(204, deleteResponse.getStatusCode());
        // Проверка, что задача действительно была удалена
        Response deletedTaskResponse = RestAssured.get("/" + taskId);
        assertEquals(404, deletedTaskResponse.getStatusCode());
    }

    @Test
    public void testInvalidTaskId() {
        // Попытка удаления задачи с недопустимым идентификатором
        String taskId = "108242";
        // Отправка DELETE-запроса для удаления задачи
        Response response = RestAssured.delete("/" + taskId);
        assertEquals(204, response.getStatusCode());
    }
}
