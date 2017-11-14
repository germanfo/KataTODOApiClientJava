/*
 *   Copyright (C) 2016 Karumi.
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package com.karumi.todoapiclient;

import com.karumi.todoapiclient.dto.TaskDto;
import java.util.List;

import com.karumi.todoapiclient.exception.ItemNotFoundException;
import com.karumi.todoapiclient.exception.UnknownErrorException;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class TodoApiClientTest extends MockWebServerTest {

  private static final String ANY_KEY = "any";
  private TodoApiClient apiClient;

  @Before public void setUp() throws Exception {
    super.setUp();
    String mockWebServerEndpoint = getBaseEndpoint();
    apiClient = new TodoApiClient(mockWebServerEndpoint);
  }

  @Test public void sendsAcceptHeader() throws Exception {
    enqueueMockResponse();

    apiClient.getAllTasks();

    assertRequestContainsHeader("Accept", "application/json");
  }


  @Test public void sendsContentTypeHeader() throws Exception {
    enqueueMockResponse();

    apiClient.getAllTasks();

    assertRequestContainsHeader("Content-Type", "application/json");
  }



  @Test public void sendsGetAllTaskRequestToTheCorrectEndpoint() throws Exception {
    enqueueMockResponse();

    apiClient.getAllTasks();

    assertGetRequestSentTo("/todos");
  }

  @Test public void parsesTasksProperlyGettingAllTheTasks() throws Exception {
    enqueueMockResponse(200, "getTasksResponse.json");

    List<TaskDto> tasks = apiClient.getAllTasks();

    assertEquals(tasks.size(), 200);
    assertTaskContainsExpectedValues(tasks.get(0));
  }

  @Test public void sendsAddTaskRequestProper() throws Exception {
    enqueueMockResponse();

    apiClient.addTask(new TaskDto("1", "2", "Finish this kata", false));

    assertRequestBodyEquals("addTaskRequest.json");
  }

  @Test (expected = UnknownErrorException.class)
  public void shouldReturnErrorCode() throws Exception {
    enqueueMockResponse(418);

    apiClient.getAllTasks();
  }

  @Test (expected = ItemNotFoundException.class)
  public void shouldReturnErrorItemNotFoundOnAnyIdTaskRequested() throws Exception {
    enqueueMockResponse(404);


    apiClient.getTaskById(ANY_KEY);
  }

  private void assertTaskContainsExpectedValues(TaskDto task) {
    assertEquals(task.getId(), "1");
    assertEquals(task.getUserId(), "1");
    assertEquals(task.getTitle(), "delectus aut autem");
    assertFalse(task.isFinished());
  }


  @Test
  public void parsesTasksProperlyGettingTaskById() throws Exception {
    enqueueMockResponse(200, "getTaskByIdResponse.json");

    TaskDto taskDto = apiClient.getTaskById("1");

    assertTaskContainsExpectedValues(taskDto);
  }

}
