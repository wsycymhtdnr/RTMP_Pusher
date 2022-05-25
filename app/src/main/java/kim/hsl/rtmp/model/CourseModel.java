package kim.hsl.rtmp.model;

import java.util.List;

/**
 * @Author: liyunfei
 * @Description:
 * @Date: 2022-05-18 05:12
 */
public class CourseModel {

    private List<ListDTO> list;
    private Integer total;

    public List<ListDTO> getList() {
        return list;
    }

    public void setList(List<ListDTO> list) {
        this.list = list;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public static class ListDTO {
        private int num;
        private String area;
        private String createTime;
        private String description;
        private String duration;
        private Integer id;
        private String thumbnail;
        private String title;
        private String type;
        private Object updateTime;
        private String url;
        private Integer userId;
        private List<?> videoTagList;

        public int getNum() {
            return (int) (100 * Math.random());
        }

        public String getArea() {
            return area;
        }

        public void setArea(String area) {
            this.area = area;
        }

        public String getCreateTime() {
            return createTime;
        }

        public void setCreateTime(String createTime) {
            this.createTime = createTime;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getDuration() {
            return duration;
        }

        public void setDuration(String duration) {
            this.duration = duration;
        }

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public String getThumbnail() {
            return thumbnail;
        }

        public void setThumbnail(String thumbnail) {
            this.thumbnail = thumbnail;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public Object getUpdateTime() {
            return updateTime;
        }

        public void setUpdateTime(Object updateTime) {
            this.updateTime = updateTime;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public Integer getUserId() {
            return userId;
        }

        public void setUserId(Integer userId) {
            this.userId = userId;
        }

        public List<?> getVideoTagList() {
            return videoTagList;
        }

        public void setVideoTagList(List<?> videoTagList) {
            this.videoTagList = videoTagList;
        }
    }

}
