import java.io.*;
import java.util.*;

public class Game implements Serializable {
    int rows, columns, imgs;
    int board[][], iniboard[][];
    int x1, y1;
    boolean pressed;
    List<Position> history;

    public void createNewGame() {
        createNewGame(8, 6, 6);
    }

    public void createNewGame(int imgs, int rows, int columns){
        // 实际数组四条边都是0不需要初始化
        this.rows = rows;
        this.columns = columns;
        this.imgs = imgs;
        this.board = new int[rows + 2][columns + 2];
        this.iniboard = new int[rows + 2][columns + 2];
        int half = columns * rows / 2;
        List<Integer> list = new ArrayList<Integer>();
        history = new ArrayList<Position>();
        int cnt = 0;
        for(int i = 1; i <= rows; i++){
            for(int j = 1; j <= columns; j++){
                int newNum;
                cnt++;
                if(cnt <= half){
                    newNum = (int)(Math.random() * 100) % imgs + 1;
                    list.add(newNum);
                } else {
                    int z=(int)(Math.random() * 100) % list.size();
                    newNum = list.remove(z);
                }
                iniboard[i][j] = board[i][j]=newNum;
            }
        }
        x1 = y1 = 0;
        pressed = false;
    }

    public void reset() {
        board = reloadInitializeArray(board);
        x1 = y1 = 0;
        pressed = false;
    }

    public static Game readGameFromFile(File f) throws Exception {
        try {
            FileInputStream fs = new FileInputStream(f);
            ObjectInputStream ois = new ObjectInputStream(fs);
            Game ret = (Game)ois.readObject();
            ois.close();
            fs.close();
            return ret;
        } catch (Exception e) {
            throw e;
        }
    }

    public void saveGameToFile(File f) throws IOException {
        try {
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(f));
            oos.writeObject(this);
            oos.close();
        } catch (Exception e) {
            throw e;
        }
    }

    public Map<String, Object> canDelete(int x, int y) {
        Map<String, Object> result = canDelete(x1, y1, x, y, board);
        if (!pressed || board[x][y] != board[x1][y1]) result.put("result", false);
        return result;
    }

    public void press(int x, int y) {
        history.add(new Position(x, y, true));
        if(!pressed || (x1 == x && y1 == y)){
            x1 = x;
            y1 = y;
            pressed = true;
            return;
        }
        // 是第二次点击
        Map<String, Object> res = canDelete(x, y);
        if ((boolean)res.get("result") == true) {
            board[x][y] = 0;
            board[x1][y1] = 0;
        }
        x1 = 0;
        y1 = 0;
        pressed = false;
    }

    class GameCannotInterpretException extends Exception {
        public GameCannotInterpretException() {}
        public GameCannotInterpretException(String message) {
            super(message);
        }
    }


    public static int[][] reloadInitializeArray(int[][] array ){
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

    public static boolean onLine(int x1,int y1,int x2,int y2,int[][] array){
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

    public static Position twoLine(int x1,int y1,int x2,int y2,int[][] array){
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

    public static Map<String,Object> threeLines(int x1,int y1,int x2,int y2,int[][] array){
        Map<String,Object> map=new HashMap<>();
        int rows=array.length;
        int columns=array[0].length;
        // 遍历棋盘，找到一个点到a点是一根线，到b点是两根线如果有就是可以连通
        for(int x=0;x<rows;x++){
            // 先列后行
            for (int y=0;y<columns;y++){
                // 先判断是不是空格
                if(array[x][y]==0){
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

    public static Map<String,Object> canDelete(int x1, int y1, int x2, int y2, int[][] array){
        boolean one=onLine(x1,y1,x2,y2,array);
        Map<String,Object> map=new HashMap<>();
        if (one){
            //System.out.println("one");
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
        boolean two = position.isFlag();
        if (two){
            //System.out.println("two");
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
}