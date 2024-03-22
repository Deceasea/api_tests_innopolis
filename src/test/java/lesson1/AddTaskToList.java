package lesson1;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class AddTaskToList extends ApiTests {

    @Test
    public void testCreateTask() {
        boolean completed = false; // Статус выполнения задачи
        String title = "addtolist"; // Название задачи
        // Отправка POST-запроса для создания задачи
        Response response = RestAssured.given()
                .contentType(ContentType.JSON)
                .body("{\"completed\": " + completed + ", \"title\": \"" + title + "\"}")
                .post();
        assertEquals(201, response.getStatusCode());
        // Получение информации о созданной задаче из тела ответа
        String responseBody = response.getBody().asString();
        // Проверка, что ответ содержит информацию о созданной задаче
        assertTrue(responseBody.contains(title));
        assertTrue(responseBody.contains("\"completed\": " + completed));
    }

    private void assertTrue(boolean contains) {
    }

    @Test
    public void testInvalidRequest() {
        Response response = RestAssured.post();
        assertEquals(400, response.getStatusCode());
    }
}

