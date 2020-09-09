package Project;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;
import java.util.TimerTask;
import java.util.Timer;

public class pacman extends JFrame {
    private GameDialog sdialog; //게임실행 다이얼로그
    private EndDialog edialog; //종료 다이얼로그
    private Explain exdialog; //설명 다이얼로그
    private LevelDialog Ldialog; //난이도 선택 다이얼로그
    private StartPanel panel = new StartPanel(); //첫 화면
    //쓰일 이미지 가져오기
    ImageIcon success = new ImageIcon("C:\\Users\\GaYeon\\Desktop\\JavaProject\\success.png"); //이미지 경로
    ImageIcon gameOver = new ImageIcon("C:\\Users\\GaYeon\\Desktop\\JavaProject\\gameover.png");
    ImageIcon cookie = new ImageIcon("C:\\Users\\GaYeon\\Desktop\\JavaProject\\cookie.png");
    ImageIcon wallPink = new ImageIcon("C:\\Users\\GaYeon\\Desktop\\JavaProject\\wallPink.jpg");
    ImageIcon wallBlue = new ImageIcon("C:\\Users\\GaYeon\\Desktop\\JavaProject\\wallBlue.png");
    ImageIcon ghost = new ImageIcon("C:\\Users\\GaYeon\\Desktop\\JavaProject\\ghost.png");
    ImageIcon pacman = new ImageIcon("C:\\Users\\GaYeon\\Desktop\\JavaProject\\pacman.jpg");
    ImageIcon pacmanLeft = new ImageIcon("C:\\Users\\GaYeon\\Desktop\\JavaProject\\pacmanLeft.jpg");
    ImageIcon empty = new ImageIcon("C:\\Users\\GaYeon\\Desktop\\JavaProject\\empty.png");
    JLabel map[][] = new JLabel[16][15]; //맵 배열
    JLabel Success = new JLabel(success); //성공시 화면아이콘
    JLabel Fail = new JLabel(gameOver); //실패 시 화면아이콘
    JLabel timerLabel = new JLabel(); //시간 부착 라벨
    TimerThread th = new TimerThread(timerLabel); //플레이 시간 측정
    int pLocateX = 14, pLocateY = 13; //팩맨의 처음위치 배열 인덱스
    int g1LocateX = 2, g1LocateY = 13; //고스트 1의 처음위치
    int g2LocateX = 14, g2LocateY = 1; //고스트 2의 처음위치
    int g3LocateX = 2, g3LocateY = 1; //고스트 3의 위치
    int cntCookie = 79; //남은쿠키 개수
    Icon tmp = cookie, tmp2;
    Random random = new Random();
    public pacman() {
        super("pacman");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setContentPane(panel); //시작 panel
        JButton start_button = new JButton("START!"); //start버튼
        JButton explain_button = new JButton("게임 설명"); //게임 설명 버튼
        setLayout(null);
        //버튼 부착
        start_button.setLocation(120, 250);
        start_button.setSize(150, 80);
        explain_button.setLocation(300, 250);
        explain_button.setSize(150, 80);
        panel.add(start_button);
        panel.add(explain_button);
        edialog = new EndDialog(this, "GameOver"); //게임종료 dialog
        exdialog = new Explain(this,"Explain"); //게임설명 dialog
        Ldialog = new LevelDialog(this,"WhatLevel"); //게임 난이도 선택 dialog
        start_button.addActionListener(new ActionListener() { //start 누를 시
            @Override
            public void actionPerformed(ActionEvent e) {
                th.start(); //타이머 시작
                Ldialog.setVisible(true); // 난이도 선택 창 실행
            }
        });
        explain_button.addActionListener(new ActionListener() {  //게임설명 버튼 누를 시
            @Override
            public void actionPerformed(ActionEvent e) {
                exdialog.setVisible(true); //설명 다이얼로그 실행
            }
        });
        setSize(600, 400);
        setVisible(true);
    }

    class StartPanel extends JPanel { //첫 시작 페이지
        private ImageIcon icon = new ImageIcon("C:\\Users\\GaYeon\\Desktop\\JavaProject\\start.png");
        private Image startImg = icon.getImage();

        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.drawImage(startImg, 0, 0, getWidth(), getHeight(), this);
        }
    }
    class LevelDialog extends JDialog{ //게임 난이도 선택 dialog
        public LevelDialog(JFrame frame,String title){
            super(frame,title, true);
            setLayout(null);
            setSize(500,300);
            getContentPane().setBackground(Color.BLACK);
            setVisible(false);
            JLabel text = new JLabel("Choose the Level!");
            JButton easy = new JButton("Easy");
            JButton hard = new JButton("Hard");
            text.setLocation(100,50);
            text.setSize(300,50);
            text.setFont(new Font("Gothic",Font.BOLD,25));
            text.setForeground(Color.white);
            easy.setLocation(100,150);
            easy.setSize(100,70);
            hard.setLocation(250,150);
            hard.setSize(100,70);
            add(text); add(easy); add(hard);
            easy.addActionListener(new ActionListener() { //easy 버튼 선택 시
                @Override
                public void actionPerformed(ActionEvent e) {
                    sdialog = new GameDialog(frame, "pacman",1); //난이도 1인 다이얼로그 생성
                    sdialog.setVisible(true);
                    Ldialog.setVisible(false);
                }
            });
            hard.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) { //hard 버튼 선택 시
                    sdialog = new GameDialog(frame, "pacman",2); //난이도 2인 다이얼로그 생성
                    sdialog.setVisible(true);
                    Ldialog.setVisible(false);
                }
            });
        }
    }

    class Explain extends JDialog { //게임 설명을 띄울 dialog
        public Explain(JFrame frame, String title){
            super(frame,title,true);
            setLayout(null);
            setSize(750,450);
            ImageIcon explain = new ImageIcon("C:\\Users\\GaYeon\\Desktop\\JavaProject\\explain.png");
            JLabel Ex = new JLabel(explain);
            Container c = getContentPane();
            Ex.setSize(getWidth(),getHeight());
            Ex.setLocation(0,0);
            c.add(Ex);
            setVisible(false);
        }
    }

    class GameDialog extends JDialog { //게임화면
        public GameDialog(JFrame frame, String title, int level) {
            super(frame, title, true);
            setLayout(new GridLayout(16, 15));
            setSize(700, 700);
            getContentPane().setBackground(Color.BLACK);
            //맵 그리기
            drawMap();

            //고스트 AI 랜덤으로 움직이게 설정
            if (level == 1) { //난이도 1 : 고스트 두마리, 속도 1초
                Timer timer = new Timer();
                MyTimeListener ghost1Move = new MyTimeListener(g1LocateX, g1LocateY, tmp, tmp2);
                MyTimeListener ghost2Move = new MyTimeListener(g2LocateX, g2LocateY, tmp, tmp2);
                timer.scheduleAtFixedRate(ghost1Move, 1000, 1000); //실행 1초 후 1초간격으로 반복
                timer.scheduleAtFixedRate(ghost2Move, 1000, 1000);
            } else if (level == 2) { //난이도 2 : 고스트 세마리, 속도 0.3초
                map[g3LocateX][g3LocateY].setIcon(ghost); //고스트 하나 더 추가
                Timer timer = new Timer();
                MyTimeListener ghost1Move = new MyTimeListener(g1LocateX, g1LocateY, tmp, tmp2);
                MyTimeListener ghost2Move = new MyTimeListener(g2LocateX, g2LocateY, tmp, tmp2);
                MyTimeListener ghost3Move = new MyTimeListener(g3LocateX, g3LocateY, tmp, tmp2);
                timer.scheduleAtFixedRate(ghost1Move, 1000, 300); //실행 1초 후 0.3초간격으로 반복
                timer.scheduleAtFixedRate(ghost2Move, 1000, 300);
                timer.scheduleAtFixedRate(ghost3Move, 1000, 300);
            }
            //키보드 이벤트 처리 (게임 start)
            addKeyListener(new MyKeyListener());
        }

        void drawMap() {  //맵 그리기
            for (int i = 0; i < 11; i++) {
                map[0][i] = new JLabel();
                map[0][i].setIcon(empty);
                add(map[0][i]);
            }
            JLabel timer = new JLabel("TI", SwingConstants.RIGHT);
            timer.setFont(new Font("Gothic", Font.BOLD, 25));
            timer.setForeground(Color.white);
            JLabel timer2 = new JLabel("ME", SwingConstants.LEFT);
            timer2.setFont(new Font("Gothic", Font.BOLD, 25));
            timer2.setForeground(Color.white);
            JLabel timer3 = new JLabel(" : ", SwingConstants.CENTER);
            timer3.setFont(new Font("Gothic", Font.BOLD, 25));
            timer3.setForeground(Color.white);
            timerLabel.setFont(new Font("Gothic", Font.BOLD, 25));
            timerLabel.setForeground(Color.white);
            add(timer);
            add(timer2);
            add(timer3);
            add(timerLabel);
            for (int i = 1; i < 16; i++) {
                for (int j = 0; j < 15; j++) {
                    map[i][j] = new JLabel();
                    map[i][j].setIcon(cookie);
                    add(map[i][j]);
                }
            }
            for (int i = 0; i < 15; i++) {
                map[1][i].setIcon(wallBlue);
                map[15][i].setIcon(wallBlue);
            }
            for (int i = 2; i < 15; i++) {
                map[i][0].setIcon(wallBlue);
                map[i][14].setIcon(wallBlue);
            }
            map[6][6].setIcon(wallPink);map[6][8].setIcon(wallPink);map[7][5].setIcon(wallPink);
            map[7][6].setIcon(wallPink);map[7][7].setIcon(wallPink);map[7][8].setIcon(wallPink);
            map[7][9].setIcon(wallPink);map[8][6].setIcon(wallPink);map[8][7].setIcon(wallPink);
            map[8][8].setIcon(wallPink);map[9][7].setIcon(wallPink);map[2][4].setIcon(wallBlue);
            map[3][4].setIcon(wallBlue);map[2][10].setIcon(wallBlue);map[3][10].setIcon(wallBlue);
            map[3][6].setIcon(wallBlue);map[3][7].setIcon(wallBlue);map[3][8].setIcon(wallBlue);
            map[4][7].setIcon(wallBlue);map[10][10].setIcon(wallBlue);map[6][2].setIcon(wallBlue);
            map[6][3].setIcon(wallBlue);map[3][12].setIcon(wallBlue);map[10][4].setIcon(wallBlue);
            map[3][2].setIcon(wallBlue);map[4][2].setIcon(wallBlue);map[4][12].setIcon(wallBlue);
            map[6][12].setIcon(wallBlue);map[6][11].setIcon(wallBlue);map[8][1].setIcon(wallBlue);
            map[8][2].setIcon(wallBlue);map[8][12].setIcon(wallBlue);map[8][13].setIcon(wallBlue);
            map[10][2].setIcon(wallBlue);map[10][3].setIcon(wallBlue);map[10][11].setIcon(wallBlue);
            map[10][12].setIcon(wallBlue);map[12][3].setIcon(wallBlue);map[12][11].setIcon(wallBlue);
            map[13][3].setIcon(wallBlue);map[4][4].setIcon(wallBlue);map[13][4].setIcon(wallBlue);
            map[13][2].setIcon(wallBlue);map[11][6].setIcon(wallBlue);map[4][10].setIcon(wallBlue);
            map[13][12].setIcon(wallBlue);map[11][8].setIcon(wallBlue);map[13][7].setIcon(wallBlue);
            map[13][10].setIcon(wallBlue);map[13][11].setIcon(wallBlue);map[14][7].setIcon(wallBlue);
            for (int i = 5; i < 11; i++) {
                for (int j = 1; j < 15; j++) {
                    if (map[i][j].getIcon().equals(cookie))
                        map[i][j].setIcon(empty);
                }
            }
            for (int i = 1; i < 14; i++) {
                if (map[7][i].getIcon().equals(empty))
                    map[7][i].setIcon(cookie);
            }
            map[8][5].setIcon(cookie);map[8][9].setIcon(cookie);map[9][6].setIcon(cookie);
            map[9][8].setIcon(cookie);map[6][5].setIcon(cookie);map[6][9].setIcon(cookie);
            map[6][7].setIcon(cookie);map[pLocateX][pLocateY].setIcon(pacman);
            map[g1LocateX][g1LocateY].setIcon(ghost);map[g2LocateX][g2LocateY].setIcon(ghost);
        }
    }

    class TimerThread extends Thread { //타이머
        private JLabel timerLabel;

        public TimerThread(JLabel timerLabel) {
            this.timerLabel = timerLabel;
        }

        public void run() {
            int n = 0; //타이머 카운트 값
            while (true) { //무한 루프
                timerLabel.setText(Integer.toString(n));
                n++;
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    return;
                }
            }
        }
    }

    class MyTimeListener extends TimerTask { //일정시간동안 반복할 task 클래스 (고스트 랜덤 움직임설정)
        Icon Tmp, Tmp2;
        int ghostX, ghostY;

        MyTimeListener(int ghostX, int ghostY, Icon Tmp, Icon Tmp2) {
            this.ghostX = ghostX;
            this.ghostY = ghostY;
            this.Tmp = Tmp;
            this.Tmp2 = Tmp2;
        }

        @Override
        public void run() {
            int rand = random.nextInt(4); //고스트가 갈 4방향중 랜덤으로 선택
            if (rand == 0) { //위
                if (!map[ghostX - 1][ghostY].getIcon().equals(wallBlue) && !map[ghostX - 1][ghostY].getIcon().equals(wallPink)
                        && !map[ghostX - 1][ghostY].getIcon().equals(ghost)) { //벽이 아니고, 다른 고스트가 아닐때
                    Tmp2 = map[ghostX - 1][ghostY].getIcon(); //갈 곳의 원래있던 이미지 저장
                    map[ghostX - 1][ghostY].setIcon(ghost); // 고스트 이동
                    map[ghostX][ghostY].setIcon(Tmp); //고스트가 있던 자리를 원래 이미지로 돌리기 (고스트는 쿠키를 먹지 않으므로 그대로 있어야함)
                    Tmp = Tmp2; //다음 이동때 원래이미지로 돌려주기 위한 저장
                    ghostX--; //고스트 위치 수정
                }
            } else if (rand == 1) { //아래
                if (!map[ghostX + 1][ghostY].getIcon().equals(wallBlue) && !map[ghostX + 1][ghostY].getIcon().equals(wallPink)
                        && !map[ghostX + 1][ghostY].getIcon().equals(ghost)) { //벽이 아니고, 다른 고스트가 아닐때
                    Tmp2 = map[ghostX + 1][ghostY].getIcon(); //갈 곳의 원래있던 이미지 저장
                    map[ghostX + 1][ghostY].setIcon(ghost); // 고스트 이동
                    map[ghostX][ghostY].setIcon(Tmp); //고스트가 있던 자리를 원래 이미지로 돌리기 (고스트는 쿠키를 먹지 않으므로 그대로 있어야함)
                    Tmp = Tmp2; //다음 이동때 원래이미지로 돌려주기 위한 저장
                    ghostX++; //고스트 위치 수정
                }
            } else if (rand == 2) { //왼쪽
                if (!map[ghostX][ghostY - 1].getIcon().equals(wallBlue) && !map[ghostX][ghostY - 1].getIcon().equals(wallPink)
                        && !map[ghostX][ghostY - 1].getIcon().equals(ghost)) { //벽이 아니고, 다른 고스트가 아닐때
                    Tmp2 = map[ghostX][ghostY - 1].getIcon(); //갈 곳의 원래있던 이미지 저장
                    map[ghostX][ghostY - 1].setIcon(ghost); // 고스트 이동
                    map[ghostX][ghostY].setIcon(Tmp); //고스트가 있던 자리를 원래 이미지로 돌리기 (고스트는 쿠키를 먹지 않으므로 그대로 있어야함)
                    Tmp = Tmp2; //다음 이동때 원래이미지로 돌려주기 위한 저장
                    ghostY--; //고스트 위치 수정
                }
            } else if (rand == 3) { //오른쪽
                if (!map[ghostX][ghostY + 1].getIcon().equals(wallBlue) && !map[ghostX][ghostY + 1].getIcon().equals(wallPink)
                        && !map[ghostX][ghostY + 1].getIcon().equals(ghost)) { //벽이 아니고, 다른 고스트가 아닐때
                    Tmp2 = map[ghostX][ghostY + 1].getIcon(); //갈 곳의 원래있던 이미지 저장
                    map[ghostX][ghostY + 1].setIcon(ghost); // 고스트 이동
                    map[ghostX][ghostY].setIcon(Tmp); //고스트가 있던 자리를 원래 이미지로 돌리기 (고스트는 쿠키를 먹지 않으므로 그대로 있어야함)
                    Tmp = Tmp2; //다음 이동때 원래이미지로 돌려주기 위한 저장
                    ghostY++; //고스트 위치 수정
                }
            }
            if (ghostX == pLocateX && ghostY == pLocateY) { //고스트와 팩맨이 만날 경우 게임오버
                cancel(); //반복되던 고스트 움직임 정지
                sdialog.setVisible(false); //게임창 사라짐
                Container c = edialog.getContentPane();
                c.setBackground(Color.BLACK);
                Fail.setSize(500, 460);
                Fail.setLocation(0, 0);
                c.add(Fail); //게임오버 이미지 부착
                edialog.setVisible(true); //edialog 띄우기
            }
        }
    }

    class MyKeyListener extends KeyAdapter { //키 이벤트
        public void keyPressed(KeyEvent e) {
            if (cntCookie == 0) { //쿠키를 다먹었을때
                th.stop(); //시간멈춤
                sdialog.setVisible(false);
                Container c = edialog.getContentPane();
                c.setBackground(Color.BLACK);
                Success.setSize(500, 400);
                Success.setLocation(10, 0);
                c.add(Success);
                JLabel result = new JLabel("TIME : " + timerLabel.getText() + "s\""); //걸린시간 부착
                result.setFont(new Font("Gothic", Font.BOLD, 35));
                result.setForeground(Color.white);
                result.setLocation(170, 305);
                result.setSize(200, 200);
                c.add(result);
                edialog.setVisible(true); //edialog 띄우기
            }
            int key = e.getKeyCode(); // 키보드 입력받기
            if (key == KeyEvent.VK_UP) { //위 방향키
                //쿠키 or 빈 곳일때
                if (map[pLocateX - 1][pLocateY].getIcon().equals(cookie) || map[pLocateX - 1][pLocateY].getIcon().equals(empty)) {
                    if (map[pLocateX - 1][pLocateY].getIcon().equals(cookie)) //쿠키인 경우
                        cntCookie--; //쿠키 감소
                    if (map[pLocateX][pLocateY].getIcon().equals(pacman)) { //팩맨이 오른쪽 얼굴일때
                        map[pLocateX - 1][pLocateY].setIcon(pacman); //팩맨이 그 자리로 움직임 (오른쪽얼굴)
                        map[pLocateX][pLocateY].setIcon(empty); //먹이가 있었다면, 먹었으니 비게 되고 원래 비어있었어도 그대로 비어있음.
                        pLocateX--; //현재위치 수정
                    } else if (map[pLocateX][pLocateY].getIcon().equals(pacmanLeft)) { //팩맨이 왼쪽얼굴일때
                        map[pLocateX - 1][pLocateY].setIcon(pacmanLeft); //팩맨이 그 자리로 움직임 (왼쪽얼굴)
                        map[pLocateX][pLocateY].setIcon(empty); //먹이가 있었다면, 먹었으니 비게 되고 원래 비어있었어도 그대로 비어있음.
                        pLocateX--; //현재위치 수정
                    }
                } else if (map[pLocateX - 1][pLocateY].getIcon().equals(ghost)) { //고스트일때
                    end(); //게임오버 출력
                }
            } else if (key == KeyEvent.VK_DOWN) { //아래 방향키
                //쿠키 or 빈곳일때
                if (map[pLocateX + 1][pLocateY].getIcon().equals(cookie) || map[pLocateX + 1][pLocateY].getIcon().equals(empty)) {
                    if (map[pLocateX + 1][pLocateY].getIcon().equals(cookie)) //쿠키인 경우
                        cntCookie--; //쿠키 감소
                    if (map[pLocateX][pLocateY].getIcon().equals(pacman)) { //팩맨이 오른쪽 얼굴일때
                        map[pLocateX + 1][pLocateY].setIcon(pacman); //팩맨이 그 자리로 움직임 (오른쪽얼굴)
                        map[pLocateX][pLocateY].setIcon(empty); //먹이가 있었다면, 먹었으니 비게 되고 원래 비어있었어도 그대로 비어있음.
                        pLocateX++; //현재위치 수정
                    } else if (map[pLocateX][pLocateY].getIcon().equals(pacmanLeft)) { //팩맨이 왼쪽 얼굴일때
                        map[pLocateX + 1][pLocateY].setIcon(pacmanLeft); //팩맨이 그 자리로 움직임 (왼쪽얼굴)
                        map[pLocateX][pLocateY].setIcon(empty); //먹이가 있었다면, 먹었으니 비게 되고 원래 비어있었어도 그대로 비어있음.
                        pLocateX++; //현재위치 수정
                    }
                } else if (map[pLocateX + 1][pLocateY].getIcon().equals(ghost)) { //고스트일때
                    end(); //게임오버
                }

            } else if (key == KeyEvent.VK_LEFT) { //왼쪽 방향키
                //쿠키 or 빈곳일때
                if (map[pLocateX][pLocateY - 1].getIcon().equals(cookie) || map[pLocateX][pLocateY - 1].getIcon().equals(empty)) {
                    if (map[pLocateX][pLocateY - 1].getIcon().equals(cookie)) //쿠키인경우
                        cntCookie--; //쿠키 감소
                    map[pLocateX][pLocateY - 1].setIcon(pacmanLeft); //팩맨이 움직임 (왼쪽얼굴)
                    map[pLocateX][pLocateY].setIcon(empty); //먹이가 있었다면, 먹었으니 비게 되고 원래 비어있었어도 그대로 비어있음.
                    pLocateY--; //현재위치 수정
                } else if (map[pLocateX][pLocateY - 1].getIcon().equals(ghost)) { //고스트일때
                    end(); //게임오버
                }
            } else if (key == KeyEvent.VK_RIGHT) { //오른쪽 방향키
                //쿠키 or 빈곳일때
                if (map[pLocateX][pLocateY + 1].getIcon().equals(cookie) || map[pLocateX][pLocateY + 1].getIcon().equals(empty)) {
                    if (map[pLocateX][pLocateY + 1].getIcon().equals(cookie)) //쿠키인경우
                        cntCookie--; //쿠키 감소
                    map[pLocateX][pLocateY + 1].setIcon(pacman); //팩맨이 움직임 (오른쪽 얼굴)
                    map[pLocateX][pLocateY].setIcon(empty); //먹이가 있었다면, 먹었으니 비게 되고 원래 비어있었어도 그대로 비어있음.
                    pLocateY++; //현재위치 수정
                } else if (map[pLocateX][pLocateY + 1].getIcon().equals(ghost)) { //고스트일때
                    end(); //게임오버
                }
            }
        }

        void end() { //게임오버 시
            sdialog.setVisible(false); //게임창 사라짐
            Container c = edialog.getContentPane();
            c.setBackground(Color.BLACK);
            Fail.setSize(500, 460);
            Fail.setLocation(0, 0);
            c.add(Fail); //게임오버 이미지 부착
            edialog.setVisible(true); //edialog 띄우기
        }
    }

    class EndDialog extends JDialog { //게임 종료시 띄울 dialog
        public EndDialog(JFrame frame, String title) {
            super(frame, title, true);
            setLayout(null);
            setSize(550, 500);
            setVisible(false);
        }
    }

    public static void main(String[] args) {
        new pacman();
    }
}