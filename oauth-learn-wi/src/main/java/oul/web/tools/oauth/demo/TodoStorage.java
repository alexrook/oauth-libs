package oul.web.tools.oauth.demo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author moroz
 */
public class TodoStorage {

    private final Map<String, Todo> internal = new HashMap<String, Todo>(15);

    public TodoStorage() {
        //fill storage dummu data for debug purposes
        for (int i = 0; i < 15; i++) {

            Todo todo = new Todo("" + 1, "" + i, "todo content" + i, new Date());
            internal.put("" + i, todo);

        }
    }

    public Todo get(String todoId) {
        return internal.get(todoId);
    }

    public String put(Todo todo) {
        internal.put(todo.getTodoId(), todo);
        return todo.getTodoId();
    }

    public String put(String userId, String content, Date date) {
        String uuid = UUID.randomUUID().toString().replaceAll("-", "");
        Todo todo = new Todo(userId, uuid, content, date);
        return put(todo);

    }

    public Collection<Todo> list(String userId) {
        Collection<Todo> ret = new ArrayList<Todo>(15);
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
