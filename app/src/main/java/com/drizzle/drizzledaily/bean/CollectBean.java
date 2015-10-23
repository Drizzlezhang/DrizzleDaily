package com.drizzle.drizzledaily.bean;

/**
 * 收藏用bean，包括文章id，标题，type类型（有大图和无大图），保存时的时间（用来排序）
 */
public class CollectBean implements Comparable<CollectBean> {
    private int id;
    private String title;
    private int type;
    private int saveTime = 0;

    public CollectBean() {
    }

    public CollectBean(int id, String title, int type, int saveTime) {
        this.id = id;
        this.saveTime = saveTime;
        this.title = title;
        this.type = type;
    }

    public int getSaveTime() {
        return saveTime;
    }

    public void setSaveTime(int saveTime) {
        this.saveTime = saveTime;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    /**
     * 重写hashcode方法，为set判断对象
     *
     * @return
     */
    @Override
    public int hashCode() {
        return id;
    }


    /**
     * 重写equals方法判断对象是否相同
     *
     * @param obj
     * @return
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        if (obj instanceof CollectBean) {
            CollectBean bean = (CollectBean) obj;
            return (bean.getId() == this.id);
        }
        return false;
    }

    /**
     * 重写比较大小方法,利于arraylist进行排序
     *
     * @param another
     * @return
     */
    @Override
    public int compareTo(CollectBean another) {
        return (another.getSaveTime() - this.saveTime);
    }
}
