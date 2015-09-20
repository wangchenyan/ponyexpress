/**
 * 2015-4-2
 */
package me.wcy.express.model;

import java.io.Serializable;

/**
 * @author wcy
 */
@SuppressWarnings("serial")
public class ResultItem implements Serializable {
    // json
    String time;
    // json
    String context;

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

}
