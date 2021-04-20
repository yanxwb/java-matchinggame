//import org.omg.CORBA.OBJ_ADAPTER;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Line2D;
import java.util.*;
import java.util.List;


/**
 * @author ZhongLingYun
 * @Description: 连连看算法工具包
 * @date 2018/10/20 13:28
 */
public class LianLianKanUtil {

    /**@Description: 棋盘数组图片初始化算法
     * @param: [countImg,jLabelArray]图片总数，按钮数组
     * @auther: ZhongLingYun
     * @date: 2018/10/20 13:34
     */
    public int[][] initializeArray(int countImg,int[][] array){
        // 实际数组四条边都是0不需要初始化
        int rows=array.length-2;
        int columns=array[0].length-2;
        int halfJLabel=columns*rows/2;
        List<Integer> list = new ArrayList();
        // 计数标记起始数目从第一个开始
        int flag=1;
        for(int x=1;x<=rows;x++){
            for(int y=1;y<=columns;y++){
                int img = 0;
                // 填充数目没有过半
                if(flag<halfJLabel+1){
                    img=(int)(Math.random()*countImg+1);
                    list.add(img);
                }else {
                    // 过半之后从集合里面拿数据保证每次出现的图片都是双数
                    int z=(int)(Math.random()*(list.size()));
                    img=list.remove(z);
                }
                array[x][y]=img;
                flag++;
            }
        }
        return array;
    }




    /**
     * @Description: 在游戏的最后防止出现四个刚好在对角线上的情况，防止出现死局
     * @auther: ZhongLingYun
     * @date: 2018/10/20 14:28
     */
    public  int[][] reloadInitializeArray(int[][] array ){
        int rows=array.length;
        int columns=array[0].length;
        int[][] newArray= new int[rows][columns];
        for(int x=1;x<=rows-2;x++){
            for (int y=1;y<=columns-2;y++){
                int selectRow=(int)(Math.random()*(rows-2))+1;
                int selectColumn=(int)(Math.random()*(columns-2))+1;
                // 表明该位置已经有图片了
                while(newArray[selectRow][selectColumn]!=0){
                    selectRow=(int)(Math.random()*(rows-2))+1;
                    selectColumn=(int)(Math.random()*(columns-2))+1;
                }
                newArray[selectRow][selectColumn]=array[x][y];
            }
        }
        System.out.println();
        for(int y=0;y<rows;y++){
            for (int x=0;x<columns;x++){
                System.out.print(newArray[y][x]+"  ");
            }
            System.out.println();

        }
        return newArray;
    }

    /**
     * @Description: 一条线可以连接图案（X/y坐标相同）
     * @auther: ZhongLingYun
     * @date: 2018/10/20 15:38
     */
    public boolean onLine(int x1,int y1,int x2,int y2,int[][] array){
        if(x1==x2 && y1==y2){
            // 不能是同一个点
            return false;
        }
        if(x1==x2||y1==y2){
            if(x1==x2){
                int min=y1<y2? y1:y2;
                int max=y1>y2? y1:y2;
                if (max-min==1){
                    // 相邻
                    return true;
                }
                for (int i=min+1;i<max;i++){
                    if(array[x1][i]!=0){
                        return false;
                    }
                }
                return true;
            }else {
                int min=x1<x2? x1:x2;
                int max=x1>x2? x1:x2;
                if(max-min==1){
                    return true;
                }
                for(int i=min+1;i<max;i++){
                    if(array[i][y1]!=0){
                        return false;
                    }
                }
                return true;
            }

        }else {
            // 不同轴
            return false;
        }
    }


    /**
     * @Description: 两根连线可以连接图案交点的两种可能（x1,y2）(x2,y1)
     * @auther: ZhongLingYun
     * @date: 2018/10/20 19:34
     */
    public Position twoLine(int x1,int y1,int x2,int y2,int[][] array){
        Map<String,Position> map= new HashMap<>();
        // 交点是(x1,y2)
        if(array[x1][y2] ==0){
            // 到(x1,y1)是否可达
            boolean firstLine =onLine(x1,y2,x1,y1,array);
            // 到(x2,y2)是否可达
            boolean secondLine=onLine(x1,y2,x2,y2,array);
            // 如果同时都可以走通那可以连线
            if(firstLine && secondLine){
                Position position= new Position(x1,y2,true);
                return position;
            }
        }
        // 交点在（x2,y1）
        if (array[x2][y1]==0){
            boolean firstLine=onLine(x1,y1,x2,y1,array);
            boolean secondLine=onLine(x2,y2,x2,y1,array);
            if(firstLine && secondLine){
                Position position= new Position(x2,y1,true);
                return  position;
            }
        }
        Position position= new Position(0,0,false);
        return position;
    }


    /**
     * @Description: 三根线可以连接两个按钮
     * @auther: ZhongLingYun
     * @date: 2018/10/20 21:29
     */
    public Map<String,Object> threeLines(int x1,int y1,int x2,int y2,int[][] array){
        Map<String,Object> map=new HashMap<>();
        int rows=array.length;
        int columns=array[0].length;
        // 遍历棋盘，找到一个点到a点是一根线，到b点是两根线如果有就是可以连通
        for(int y=0;y<rows;y++){
            // 先列后行
            for (int x=0;x<columns;x++){
                // 先判断是不是空格
                if(array[y][x]==0){
                    boolean flag=onLine(x,y,x1,y1,array);
                    if(flag) {
                        Position position = twoLine(x, y, x2, y2, array);
                        boolean result=position.isFlag();
                        // 这个点能够和被点击的两个点连通，同时返回一个中转点
                        if (result) {
                            List<Position> list= new ArrayList<>();
                            list.add(new Position(x1,y1,true));
                            list.add(new Position(x,y,true));
                            list.add(position);
                            list.add(new Position(x2,y2,true));
                            map.put("list",list);
                            map.put("result",true);
                            return map;
                        }
                    }
                    // 这个点可能到a点是两根线到b点是一根线
                    Position position=twoLine(x,y,x1,y1,array);
                    flag=position.isFlag();
                    if (flag){
                        boolean result=onLine(x,y,x2,y2,array);
                        if (result){
                            List<Position> list= new ArrayList<>();
                            list.add(new Position(x1,y1,true));
                            list.add(position);
                            list.add(new Position(x,y,true));
                            list.add(new Position(x2,y2,true));
                            map.put("list",list);
                            map.put("result",true);
                            return map;
                        }
                    }
                }
            }
        }
        map.put("result",false);
        return map;

    }

    /**
     * @Description: 两个点直接是否可以通过连线消除
     * @auther: ZhongLingYun
     * @date: 2018/10/25 10:22
     */
    public Map<String,Object> canDelete(int x1, int y1, int x2, int y2, int[][] array,JPanel centerPanel){
        boolean one=onLine(x1,y1,x2,y2,array);
        Map<String,Object> map=new HashMap<>();
        if (one){
            System.out.println("one");
            List<Position> list=new ArrayList<>();
            list=Arrays.asList(
                    new Position(x1,y1,true),
                    new Position(x2,y2,true)
            );
            map.put("list",list);
            map.put("result",true);
            return map;
        }
        Position position=twoLine(x1,y1,x2,y2,array);
        boolean  two =position.isFlag();
        if (two){
            System.out.println("two");
            List<Position> list=new ArrayList<>();
            list.add(new Position(x1,y1,true));
            list.add(position);
            list.add(new Position(x2,y2,true));
            map.put("list",list);
            map.put("result",true);
            return map;
        }
       return map=threeLines(x1,y1,x2,y2,array);
    }

    /**
     * @Description: 两个点之间的连线
     * @auther: ZhongLingYun
     * @date: 2018/10/25 10:23
     */
    public void dwLine(List<Position> list,JPanel center){
        int width=center.getWidth();
        int height=center.getHeight();
        for(int i=0;i<list.size()-1;i++){
             // 最后一个点没有下个连接点
             Position position1=list.get(i);
             Position position2=list.get(i+1);
             int x1=position1.getX();
             int y1=position1.getY();
             int x2=position2.getX();
             int y2=position2.getY();
             double x3=width/8*(y1+0.5);
             double y3=height/8*(x1+0.5);
             double x4=width/8*(y2+0.5);
             double y4=height/8*(x2+0.5);
             Graphics2D graphics2D=(Graphics2D)center.getGraphics();
             graphics2D.setColor(Color.red);
             Line2D line2D = new Line2D.Double(x3, y3,x4, y4);
             graphics2D.draw(line2D);
         }

    }
}


