package oul.web.tools.oauth.demo;

import java.util.Date;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author moroz
 */
@XmlRootElement
public class Todo {

    private Date update;

    private String userId, todoId, content;

    public Todo() {
    }

    public Todo(String userId, String todoId, String content, Date update) {
        this.userId = userId;
        this.content = content;
        this.update = update;
        this.todoId = todoId;
    }

    public Date getUpdate() {
        return update;
    }

    public void setUpdate(Date update) {
        this.update = update;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTodoId() {
        return todoId;
    }

    public void setTodoId(String todoId) {
        this.todoId = todoId;
    }

}
