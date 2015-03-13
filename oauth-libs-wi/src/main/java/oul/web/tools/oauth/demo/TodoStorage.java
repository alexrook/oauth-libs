package oul.web.tools.oauth.demo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import javax.inject.Singleton;

/**
 * @author moroz
 */
@Singleton
public class TodoStorage {

    public static class TodoNotFoundException extends Exception {

        public TodoNotFoundException() {
        }

        public TodoNotFoundException(String message) {
            super(message);
        }

    }

    private final Map<String, Todo> internal = new HashMap<String, Todo>();

    public TodoStorage() {
        //fill storage dummy data for debug purposes
        for (int i = 0; i < 3; i++) {

            Todo todo = new Todo("" + 1, "" + i, "todo content" + i, new Date());
            internal.put("" + i, todo);

        }
    }

    public Todo get(String todoId) {
        return internal.get(todoId);
    }

    public String delete(String todoId) throws TodoNotFoundException {

        if (internal.containsKey(todoId)) {
            internal.remove(todoId);
            return todoId;
        } else {
            throw new TodoNotFoundException("updated todo with todoId="
                    + todoId + " not found in storage");
        }

    }

    public String put(Todo todo) {
        internal.put(todo.getTodoId(), todo);
        return todo.getTodoId();
    }

    public String edit(String todoId, String content, Date date) throws TodoNotFoundException {
        Todo todo = internal.get(todoId);
        if (todo != null) {
            todo.setContent(content);
            todo.setUpdate(date);
            return put(todo);
        } else {
            throw new TodoNotFoundException("updated todo with todoId=" + todoId + " not found in storage");
        }
    }

    public String put(String userId, String content, Date date) {
        String uuid = UUID.randomUUID().toString().replaceAll("-", "");
        Todo todo = new Todo(userId, uuid, content, date);
        return put(todo);

    }

    public Collection<Todo> list(String userId) {
        Collection<Todo> ret = new ArrayList<Todo>();
        for (String key : internal.keySet()) {
            Todo buf = internal.get(key);
            if (buf.getUserId().equalsIgnoreCase(userId)) {
                ret.add(buf);
            }
        }

        return ret;
    }

    public Collection<Todo> listAll() {
        return internal.values();
    }
}
