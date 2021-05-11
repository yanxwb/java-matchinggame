import javax.swing.*;
import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.lang.Thread;

public class GameView implements ActionListener {
    // 棋盘的总行数和总列数
    private Game game;

    private int width = 800;
    private int height = 800;

    private int x = 800;
    private int y = 150;

    int cnt = 0;
    // 主面板
    private JFrame mainFrame;

    private Container thisContainer;

    // 子面板
    private JPanel centerPanel, southPanel, northPanel;

    // 重列，重新开始按钮
    private JButton resetButton, newlyButton, saveButton, loadButton, webButton, loadplaybackButton, loadfromwebButton,
            loadplayfromwebButton;

    // 界面按钮数组,和棋盘数组 一样大
    private JButton[][] allButton;

    private Image images[];

    // 初始化界面
    public void init() {
        game = new Game();
        game.createNewGame();
        mainFrame = new JFrame("连连看");
        thisContainer = mainFrame.getContentPane();
        thisContainer.setLayout(new BorderLayout());
        centerPanel = new JPanel();
        southPanel = new JPanel();
        northPanel = new JPanel();
        thisContainer.add(centerPanel, "Center");
        thisContainer.add(southPanel, "South");
        thisContainer.add(northPanel, "North");
        centerPanel.setLayout(new GridLayout(game.rows + 2, game.columns + 2));
        images = new Image[game.imgs + 1];
        for (int i = 1; i <= game.imgs; ++i) {
            images[i] = new ImageIcon("source/" + i + ".png").getImage();
        }
        this.allButton = new JButton[game.rows + 2][game.columns + 2];
        for (int i = 0; i <= game.rows + 1; i++) {
            for (int j = 0; j <= game.columns + 1; j++) {
                allButton[i][j] = new JButton();
                allButton[i][j].addActionListener(this);
                allButton[i][j].setBackground(Color.WHITE);
                centerPanel.add(allButton[i][j]);
            }
        }

        resetButton = new JButton("重列");
        resetButton.addActionListener(this);
        newlyButton = new JButton("再来一局");
        newlyButton.addActionListener(this);
        saveButton = new JButton("存储游戏");
        saveButton.addActionListener(this);
        loadButton = new JButton("导入游戏");
        loadButton.addActionListener(this);
        loadplaybackButton = new JButton("导入回放");
        loadplaybackButton.addActionListener(this);
        webButton = new JButton("上传到网络");
        webButton.addActionListener(this);
        loadfromwebButton = new JButton("从网络导入游戏");
        loadfromwebButton.addActionListener(this);
        loadplayfromwebButton = new JButton("从网络导入回放");
        loadplayfromwebButton.addActionListener(this);
        southPanel.add(resetButton);
        southPanel.add(newlyButton);
        southPanel.add(saveButton);
        southPanel.add(loadButton);
        southPanel.add(loadplaybackButton);
        southPanel.add(webButton);
        southPanel.add(loadfromwebButton);
        southPanel.add(loadplayfromwebButton);
        mainFrame.setBounds(x, y, width, height);
        mainFrame.setVisible(true);
        centerPanel.setVisible(true);
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                drawBoard(game.x1, game.y1);
            }
        });
        drawBoard(0, 0);
    }

    public void operation(int x, int y) {
        for (int i = 0; i <= game.rows + 1; i++) {
            for (int j = 0; j <= game.columns + 1; j++) {
                System.out.print(game.board[i][j]);
                System.out.print(" ");
            }
            System.out.println("");
        }
        System.out.println(x + " " + y);
        Map<String, Object> res = game.canDelete(x, y);
        if ((boolean) res.get("result") == true) {
            List<Position> lis = (List<Position>) res.get("list");
            drawLine(lis);
        }
        game.press(x, y);
        drawBoard(x, y);
        boolean win = true;
        for (int i = 1; i <= game.rows; i++) {
            for (int j = 1; j <= game.rows; j++) {
                if (game.board[i][j] != 0) {
                    win = false;
                    break;
                }
            }
        }
        if (win) {
            JDialog dialog = new JDialog(mainFrame);
            dialog.setSize(200, 200);
            dialog.setLocation(500, 500);
            dialog.setLayout(new BorderLayout());
            dialog.add(new JLabel("您赢了！", JLabel.CENTER));
            dialog.setVisible(true);
        }
    }

    public void drawLine(List<Position> list) {
        int width = centerPanel.getWidth();
        int height = centerPanel.getHeight();
        for (int i = 0; i < list.size() - 1; i++) {
            // 最后一个点没有下个连接点
            Position position1 = list.get(i);
            Position position2 = list.get(i + 1);
            int x1 = position1.getX();
            int y1 = position1.getY();
            int x2 = position2.getX();
            int y2 = position2.getY();
            double x3 = (double) width / (game.columns + 2) * (y1 + 0.5);
            double y3 = (double) height / (game.rows + 2) * (x1 + 0.5);
            double x4 = (double) width / (game.columns + 2) * (y2 + 0.5);
            double y4 = (double) height / (game.rows + 2) * (x2 + 0.5);
            Graphics2D graphics2D = (Graphics2D) centerPanel.getGraphics();
            graphics2D.setColor(new Color(255, 117, 0));
            Line2D line2D = new Line2D.Double(x3, y3, x4, y4);
            graphics2D.draw(line2D);
            System.err.println(x1 + " " + y1 + " " + x2 + " " + y2);
        }
        try {
            Thread.sleep(300);
        } catch (Exception e) {
        }
        centerPanel.repaint();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // 如果是新开一局
        if (e.getSource() == newlyButton) {
            game.createNewGame();
            drawBoard(0, 0);
        }
        // 重新布局剩余的图片
        if (e.getSource() == resetButton) {
            game.reset();
            drawBoard(0, 0);
        }
        // 导入游戏
        if (e.getSource() == loadButton) {
            javax.swing.filechooser.FileSystemView fsv = new DirectoryRestrictedFileSystemView(new File("./data"));
            JFileChooser fileChooser = new JFileChooser(fsv);
            fileChooser.setCurrentDirectory(new File("./data"));
            fileChooser.showOpenDialog(mainFrame);
            File f = fileChooser.getSelectedFile();
            try {
                game = Game.readGameFromStream(new FileInputStream(f));
                drawBoard(0, 0);
            } catch (Exception ee) {
                System.out.println(ee);
            }
        }
        // 导入回放
        if (e.getSource() == loadplaybackButton) {
            javax.swing.filechooser.FileSystemView fsv = new DirectoryRestrictedFileSystemView(new File("./data"));
            JFileChooser fileChooser = new JFileChooser(fsv);
            fileChooser.setCurrentDirectory(new File("./data"));
            fileChooser.showOpenDialog(mainFrame);
            File f = fileChooser.getSelectedFile();
            try {
                game = Game.readGameFromStream(new FileInputStream(f));
                replay();
            } catch (Exception ee) {
                System.out.println(ee);
            }
        }
        // 存储游戏
        if (e.getSource() == saveButton) {
            javax.swing.filechooser.FileSystemView fsv = new DirectoryRestrictedFileSystemView(new File("./data"));
            JFileChooser fileChooser = new JFileChooser(fsv);
            fileChooser.setCurrentDirectory(new File("./data"));
            fileChooser.showSaveDialog(mainFrame);
            File f = fileChooser.getSelectedFile();
            try {
                game.saveGameToStream(new FileOutputStream(f));
            } catch (IOException ee) {
                System.out.println(ee);
            }
        }
        // 存储到网络
        if (e.getSource() == webButton) {
            cnt++;
            Client c = new Client();
            File f = new File(c.dir + "onlineSave" + cnt);
            try {
                game.saveGameToStream(new FileOutputStream(f));
                c.sendFile(f.getAbsolutePath());
                System.out.println(f.getName());
            } catch (IOException ee) {
                System.out.println("get IOExecption when sending file. ");
            }
        }
        // 从网络导入游戏
        if (e.getSource() == loadfromwebButton) {
            try {
                Client c = new Client();
                c.getFile("OnlineSave1");
            } catch (IOException ee) {
                System.out.println("get IOExecption when getting file. ");
            }
        }
        // 从网络导入回放
        if (e.getSource() == loadplayfromwebButton) {
            javax.swing.filechooser.FileSystemView fsv = new DirectoryRestrictedFileSystemView(
                    new File("./OnlineData"));
            JFileChooser fileChooser = new JFileChooser(fsv);
            fileChooser.setCurrentDirectory(new File("./OnlineData"));
            fileChooser.showOpenDialog(mainFrame);
            File f = fileChooser.getSelectedFile();
            try {
                game = Game.readGameFromStream(new FileInputStream(f));
                replay();
            } catch (Exception ee) {
                System.out.println(ee);
            }
        }

        // 监控棋盘
        for (int i = 1; i <= game.rows; i++) {
            for (int j = 1; j <= game.columns; j++) {
                if (e.getSource() == allButton[i][j]) {
                    System.out.println(i + " " + j);
                    operation(i, j);
                }
            }
        }
    }

    public void drawBoard(int x, int y) {
        int iconSize = Math.min(centerPanel.getWidth() / (game.columns + 2), centerPanel.getHeight() / (game.rows + 2))
                * 3 / 5;
        System.out.println(centerPanel.getSize());
        for (int i = 0; i <= game.rows + 1; i++) {
            for (int j = 0; j <= game.columns + 1; j++) {
                // allButton[i][j].setText(game.board[i][j] + "");
                allButton[i][j].setText("");
                if (game.board[i][j] != 0) {
                    Image newimg = images[game.board[i][j]].getScaledInstance(iconSize, iconSize, Image.SCALE_SMOOTH);
                    allButton[i][j].setIcon(new ImageIcon(newimg));
                }
                allButton[i][j].setVisible(game.board[i][j] != 0);
                allButton[i][j].setFocusPainted(game.pressed && i == x && j == y);
            }
        }
        centerPanel.repaint();
    }

    public void replay() {
        new Thread(new Runnable() {
            public void run() {
                for (int i = 0; i <= game.rows + 1; i++) {
                    for (int j = 0; j <= game.columns + 1; j++) {
                        game.board[i][j] = game.iniboard[i][j];
                    }
                }
                List<Position> history = game.history;
                game.history = new ArrayList<Position>();
                game.pressed = false;
                game.x1 = game.y1 = 0;
                drawBoard(0, 0);
                try {
                    Thread.sleep(300);
                } catch (Exception e) {
                }
                for (int i = 0; i < history.size(); i++) {
                    int x = history.get(i).getX(), y = history.get(i).getY();
                    operation(x, y);
                    allButton[6][6].setFocusPainted(true);
                    centerPanel.repaint();
                    try {
                        Thread.sleep(300);
                    } catch (Exception e) {
                    }
                }
            }
        }).start();
    }
}