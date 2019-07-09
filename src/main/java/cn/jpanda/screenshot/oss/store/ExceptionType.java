package cn.jpanda.screenshot.oss.store;

public class ExceptionType {
    protected String description;
    protected Integer level;

    public ExceptionType(String description, Integer level) {
        this.description = description;
        this.level = level;
    }

    public ExceptionType() {
    }

    public String getDescription() {
        return this.description;
    }

    public Integer getLevel() {
        return level;
    }
}
