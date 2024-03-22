package lesson1;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.Test;

import static io.restassured.RestAssured.given;
import static org.junit.Assert.assertEquals;

public class RenameTask extends ApiTests {
    @Test
    public void testUpdateTask() {
        // Создание новой задачи
        String title = "New Task";
        Response createResponse = given()
                .contentType(ContentType.JSON)
                .body("{\"title\": \"" + title + "\"}")
                .post();
        assertEquals(201, createResponse.getStatusCode());

        // Получение идентификатора созданной задачи из тела ответа
        String taskId = createResponse.jsonPath().getString("id");
        assertNotNull(taskId);

        String newTitle = "Updated Title";

        // Отправка PATCH-запроса для обновления задачи
        Response updateResponse = given()
                .contentType(ContentType.JSON)
                .body("{\"title\": \"" + newTitle + "\"}")
                .patch("/" + taskId);
        assertEquals(200, updateResponse.getStatusCode());

        // Проверка, что задача действительно была обновлена
        Response updatedTaskResponse = RestAssured.get("/" + taskId);
        assertEquals(200, updatedTaskResponse.getStatusCode());

        // Проверка, что новое название задачи совпадает с ожидаемым значением
        String updatedTitle = updatedTaskResponse.jsonPath().getString("title");
        assertEquals(newTitle, updatedTitle);
    }

    private void assertNotNull(String taskId) {
    }

    @Test
    public void testInvalidTaskId() {
        // Попытка переименования задачи с недопустимым идентификатором
        String taskId = "invalidTaskId";
        String newTitle = "New Title";

        Response response = given()
                .contentType(ContentType.JSON)
                .body("{\"title\": \"" + newTitle + "\"}")
                .post("/" + taskId);
        assertEquals(404, response.getStatusCode());
    }
}
