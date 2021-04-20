/**
 * @author ZhongLingYun
 * @Title: Position
 * @Description: 位置实体
 * @date 2018/10/2510:28
 */
public class Position {

    /**
     * x坐标
     * */
    private int x;

    /**
     * y坐标
     */
    private int y;

    /**
     *  这个点是否是另外两个点的转折点
     */
    private boolean flag;

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public boolean isFlag() {
        return flag;
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }

    @Override
    public String toString() {
        return "Position{" +
                "x=" + x +
                ", y=" + y +
                ", flag=" + flag +
                '}';
    }

    public Position(int x, int y, boolean flag) {
        this.x = x;
        this.y = y;
        this.flag = flag;
    }
}
