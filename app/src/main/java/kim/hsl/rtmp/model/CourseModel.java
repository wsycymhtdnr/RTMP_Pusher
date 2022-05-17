package kim.hsl.rtmp.model;

/**
 * @Author: liyunfei
 * @Description:
 * @Date: 2022-05-18 05:12
 */
public class CourseModel {
    private String imgString;
    private String title;
    private int num;

    public CourseModel(String imgString, String title, int num) {
        this.imgString = imgString;
        this.title = title;
        this.num = num;
    }

    public String getImgString() {
        return imgString;
    }

    public void setImgString(String imgString) {
        this.imgString = imgString;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }
}
