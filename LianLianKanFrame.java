import javax.imageio.IIOException;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.*;
import java.util.List;
import java.util.Map;

/**
 * @author ZhongLingYun
 * @Title: LianLianKanFrame
 * @Description: 连连看主面板
 * @date 2018/10/2122:00
 */
public class LianLianKanFrame implements ActionListener {
    // 棋盘的总行数和总列数
    private  int countRows=8;
    private  int countColumns=8;
    // 9张图
    private  int countImg=9;

    private int  width=800;
    private int height=800;

    private int x=800;
    private int y=150;

    // 主面板
    private JFrame mainFrame;

    private Container thisContainer;

    // 子面板
    private JPanel centerPanel, southPanel, northPanel;

    // 重列，重新开始按钮
    private JButton  resetButton, newlyButton, saveButton, webButton;

    // 界面按钮数组,和棋盘数组 一样大
    private JButton[][] allButton = new JButton[countColumns][countRows];

    // 第一个被点击的按钮
    private JButton firstJButton;

    // 第二个被点击的按钮
    private JButton secondJButton;

    // 二维数据比实际棋盘多出四条边
    private int[][] arry = new int[countColumns][countRows];

    private final LianLianKanUtil lianLianKanUtil = new LianLianKanUtil();

    // 第一次被点击的按钮的数据
    private  int firstMsg;

    // 第二次被点击的按钮的数据
    private int secondMsg;

    // 记录是否之前有一个按钮被选中
    private boolean press=false;

    // 两个被选中的位置
    private static int x1,y1;

    private static  int x2,y2;


    // 初始化界面
    public void init(){
        mainFrame = new JFrame("连连看");
        thisContainer = mainFrame.getContentPane();
        thisContainer.setLayout(new BorderLayout());
        centerPanel = new JPanel();
        southPanel = new JPanel();
        northPanel = new JPanel();
        thisContainer.add(centerPanel,"Center");
        thisContainer.add(southPanel,"South");
        thisContainer.add(northPanel,"North");
        centerPanel.setLayout(new GridLayout(countRows, countColumns));
        // 初始化棋盘数组
        arry=lianLianKanUtil.initializeArray(countImg,arry);
        // 初始化棋盘按钮
        for (int row = 0; row < countRows; row++) {
            for (int column = 0; column < countColumns; column++) {
                // 将数组中的值赋值给按钮
                allButton[row][column] = new JButton(String.valueOf(arry[row ][column ]));
                if(arry[row ][column ]==0){
                    allButton[row][column].setVisible(false);
                }
                // 设置监听
                allButton[row][column].addActionListener(this);
                // 将按钮添加到面板上
                centerPanel.add(allButton[row][column]);
            }
        }
        resetButton = new JButton("重列");
        resetButton.addActionListener(this);
        newlyButton = new JButton("再来一局");
        newlyButton.addActionListener(this);
        saveButton = new JButton("存储当前游戏");
        saveButton.addActionListener(this);
        webButton = new JButton("上传到网络");
        webButton.addActionListener(this);
        southPanel.add(resetButton);
        southPanel.add(newlyButton);
        southPanel.add(saveButton);
        southPanel.add(webButton);
        mainFrame.setBounds(x, y, width, height);
        mainFrame.setVisible(true);
        centerPanel.setVisible(true);
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    }

    // 进行能否移除操作
    public void operation(int row,int column){
        if(press){
            // 如果是第二次点击
            secondJButton=allButton[row][column];
            x2=row;
            y2=column;
            secondMsg=arry[x2][y2];
            if(secondJButton!=firstJButton){
                // 如果不是同一个按钮
                if(secondMsg==firstMsg){
                    // 值相等
                    Map<String,Object> map =lianLianKanUtil.canDelete(x1,y1,x2,y2,arry,centerPanel);
                    boolean flag= (boolean) map.get("result");
                    if(flag){
                        // 可以通过路线删除
                        List<Position> list= (List<Position>) map.get("list");
                        lianLianKanUtil.dwLine(list,centerPanel);
                        try {
                            Thread.sleep(2000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        centerPanel.repaint();
                        arry[x1][y1]=0;
                        arry[x2][y2]=0;
                        allButton[x1][y1].setVisible(false);
                        allButton[x2][y2].setVisible(false);
                        press=false;

                        System.out.println();
                        for(int y=0;y<countColumns;y++){
                            for (int x=0;x<countRows;x++){
                                System.out.print(arry[y][x]+"  ");
                            }
                            System.out.println();
                        }
                    }else {
                        // 不能通过路线删除
                        firstJButton=secondJButton;
                        secondJButton=null;
                        firstMsg=secondMsg;
                        secondMsg=0;
                        x1=x2;
                        x2=0;
                        y1=y2;
                        y2=0;
                        press=true;
                    }
                }else {
                    firstJButton=secondJButton;
                    secondJButton=null;
                    firstMsg=secondMsg;
                    secondMsg=0;
                    x1=x2;
                    x2=0;
                    y1=y2;
                    y2=0;
                    press=true;
                }
            }else {
                // 同一个按钮
                firstJButton=secondJButton;
                secondJButton=null;
                firstMsg=secondMsg;
                secondMsg=0;
                x1=x2;
                x2=0;
                y1=y2;
                y2=0;
                press=true;
            }
        }else {
            // 如果是第一次点击
            firstMsg=arry[row][column];
            firstJButton=allButton[row][column];
            x1=row;
            y1=column;
            press=true;
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // 如果是新开一局
        if (e.getSource() == newlyButton) {
            // 移除主面板
            centerPanel.setVisible(false);
            centerPanel= new JPanel();
            press=false;
            arry=lianLianKanUtil.initializeArray(countImg,arry);
            thisContainer.add(centerPanel,"Center");
            centerPanel.setLayout(new GridLayout(countRows, countColumns));
            for (int row = 0; row < countRows; row++) {
                for (int column = 0; column < countColumns; column++) {
                        // 将数组中的值赋值给按钮
                        allButton[row][column] = new JButton(String.valueOf(arry[row][column]));
                        if (arry[row][column]==0){
                            allButton[row][column].setVisible(false);
                        }
                        // 设置监听
                        allButton[row][column].addActionListener(this);
                    // 将按钮添加到面板上
                    centerPanel.add(allButton[row][column]);
                }
            }
        }
        // 重新布局剩余的图片
        if (e.getSource()==resetButton){
            arry=lianLianKanUtil.reloadInitializeArray(arry);
            centerPanel.setVisible(false);
            centerPanel= new JPanel();
            thisContainer.add(centerPanel,"Center");
            centerPanel.setLayout(new GridLayout(countRows, countColumns));
            for (int row = 0; row < countRows; row++) {
                for (int column = 0; column < countColumns; column++) {
                   if(arry[row][column]==0){
                       allButton[row][column].setVisible(false);
                   }else {
                       // 将数组中的值赋值给按钮
                       allButton[row][column] = new JButton(String.valueOf(arry[row ][column]));
                       // 设置监听
                       allButton[row][column].addActionListener(this);
                   }
                    // 将按钮添加到面板上
                    centerPanel.add(allButton[row][column]);
                }
            }

        }
        // 存储当前游戏
        if (e.getSource() == saveButton) {
            String fileName = "data/game.txt";
            try(FileOutputStream fos = new FileOutputStream(fileName);
                OutputStreamWriter osw = new OutputStreamWriter(fos);
                BufferedWriter bw = new BufferedWriter(osw);){
                for (int row=0;row<countRows;row++){
                    for (int column=0;column<countColumns;column++){
                        bw.write(arry[row][column]+" ");
                    }
                    bw.write("\n");
                }
                bw.flush();
            } catch(IOException ee) {System.out.println("IOex");}
        }
        if (e.getSource() == webButton) {
            String hostName = "47.94.82.10";
            try {
                Socket s = new Socket(hostName, 888);
                OutputStream os = s.getOutputStream();
                BufferedOutputStream bs = new BufferedOutputStream(os);
                for (int row=0;row<countRows;row++){
                    for (int column=0;column<countColumns;column++){
                        String str = arry[row][column]+" ";
                        for (int i = 0; i < str.length(); i++) {
                            bs.write(str.charAt(i));
                        }
                    }
                    bs.write('\n');
                }
                s.close();
            } catch(IOException ee) {System.out.println(ee.toString());}
        }
        // 监控棋盘
        for (int row=0;row<countRows;row++){
            for (int column=0;column<countColumns;column++){
                if (e.getSource()==allButton[row][column]){
                    operation(row,column);
                }
            }
        }
    }
}




