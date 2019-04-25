package AwtSwing;
import java.awt.*;
import java.awt.event.*;
import java.util.LinkedList;
import java.util.Random;

import javax.swing.*;

public class SnakeGame2 {
	
	static class MyFrame extends JFrame {
		static class XY {
			int x;
			int y;
			public XY(int x, int y) {
				this.x = x;
				this.y = y;
			}
		}
		static JPanel panelNorth;
		static JPanel panelCenter;
		static JLabel labelTitle;
		static JLabel labelMessage; //250ms * 40ĭ
		static JPanel[][] panels = new JPanel[20][20];
		static int[][] map = new int[20][20]; //���� ��ġ 9, ��ź 8
		static LinkedList<XY> snake = new LinkedList<XY>();
		static int dir = 3; //������� 0:up,1:down
		                    //2:left,3:right
		static int score = 0; //����
		static int time = 0; //����ð�
		static int timeCount = 0; //200ms���� ī��Ʈ�� ����
		static Timer timer = null; //���� ������ ó��,
		                            //����(����,�浹)
		static int timeCountFruit = 0;
		
		public MyFrame(String title) {
			super(title);
			this.setSize(400,500);
			this.setVisible(true);
			this.setDefaultCloseOperation(
					JFrame.EXIT_ON_CLOSE);
			initUI();
			makeSnakeList();
			startTimer(); //1000ms���� -> 200ms
			setKeyListener(); //Ű���� �̺�Ʈ üũ
			makeFruit();
		}
		void removeFruit() {
			for(int i=0; i<20; i++) {
				for(int j=0; j<20; j++) {
					if(map[i][j] == 9) {//����
						map[i][j] = 0;
					}
				}
			}
		}
		void removeBomb() {
			for(int i=0; i<20; i++) {
				for(int j=0; j<20; j++) {
					if(map[i][j] == 8) {//��ź ����
						map[i][j] = 0;
					}
				}
			}
		}
		void makeFruit() {
			Random rand = new Random();
			//0~19, 0~19
			int randX = rand.nextInt(20);
			int randY = rand.nextInt(20);
			map[randX][randY] = 9; //���� ��ġ!
		}
		void setKeyListener() {
			this.addKeyListener(new KeyAdapter() {
				public void keyPressed(KeyEvent e) {
					if(e.getKeyCode() == KeyEvent.VK_UP) {
						if(dir!=1)//down
							dir = 0;
					}
					else if(e.getKeyCode() == KeyEvent.VK_DOWN) {
						if(dir!=0)
							dir = 1;
					}
					else if(e.getKeyCode() == KeyEvent.VK_LEFT) {
						if(dir!=3)
							dir = 2;
					}
					else if(e.getKeyCode() == KeyEvent.VK_RIGHT) {
						if(dir!=2)
							dir = 3;
					}
				}
			});
		}
		void makeBomb() {
			Random rand = new Random();
			int randX = 0;
			int randY = 0;
			//�ߺ����� - ����,��ź,���� �Ӹ�,��ü�϶� �ٽ� ��÷
			boolean isRetry = true;
			while(isRetry) {
				randX = rand.nextInt(20);
				randY = rand.nextInt(20);
				boolean isSnakeBody = false;
				for(XY xy : snake) {
					if(xy.x == randX && xy.y == randY) {
						isSnakeBody = true;
					}
				}
				if(isSnakeBody==true) { //���� ��ü�ΰ�?
					isRetry = true;//����÷��
				}else if(map[randX][randY]==9) { //�����̸�,
					isRetry = true; //����÷��
				}else if(map[randX][randY]==8) { //��ź�̸�,
					isRetry = true;	//����÷��
				}else {
					isRetry = false;//����÷ ����
				}
				
			}
			
			map[randX][randY] = 8;//��ź ��ġ
			
		}
		
		void startTimer() {
			//�͸�Ŭ����+�������̵�
			timer = new Timer(50, new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					timeCount++;
					
					if(score < 500) {
						if(timeCount%5==0) { //250ms���� ������
							//10�� 40ī��Ʈ*5��
							if(timeCount%200 == 0) {
								removeFruit();
								makeFruit();
								removeBomb();
								makeBomb();
							}
							moveSnake();
							updateUI();		
						}
					}else if(score < 1000) {
						if(timeCount%2==0) { //100ms���� ������
							//4�� 40ī��Ʈ*2��
							if(timeCount%80 == 0) {
								removeFruit();
								makeFruit();
								removeBomb();
								makeBomb();
							}
							moveSnake();
							updateUI();		
						}
					}else {  			//50ms����
						//2�� 20ī��Ʈ
						if(timeCount%50 == 0) {
							removeFruit();
							makeFruit();
						}
						moveSnake();
						updateUI();
					}
					
				}
			});
			timer.start();
		}
		void updateUI() { //��� UI ǥ��!
			labelTitle.setText("����:"+score+"�� �ð�:"+time+"��");
			
			for(int i=0; i<20; i++) {
				for(int j=0; j<20; j++) {
					
					if(map[i][j]==9) { //�����̸�,
						panels[i][j].setBackground(Color.CYAN);
					}else if(map[i][j]==8) { //��ź�̸�,
						panels[i][j].setBackground(Color.MAGENTA);
					}else { //�Ϲݻ�
						panels[i][j].setBackground(Color.GRAY);
					}
				}
			}
			
			int index = 0;
			boolean addTail = false;
			for(XY xy : snake) {
				if(index==0) { //Head
					panels[xy.y][xy.x].setBackground(Color.RED);
					if(map[xy.y][xy.x]==9) {
						map[xy.y][xy.x] = 0;
						makeFruit();
						score += 100;
						addTail = true;
					}
					
				}else { //Body,Tail
					panels[xy.y][xy.x].setBackground(Color.BLUE);
				}
				index++;
			}
			if(addTail==true)
				addTail();
		}
		void addTail() {
			int tailX = snake.get(snake.size()-1).x;
			int tailY = snake.get(snake.size()-1).y;
			int tailX2 = snake.get(snake.size()-2).x;
			int tailY2 = snake.get(snake.size()-2).y;
			
			if(tailX<tailX2) { //���ʿ� ����.
				snake.add(new XY(tailX-1,tailY));
			}
			else if(tailX>tailX2) { //�����ʿ� ����
				snake.add(new XY(tailX+1,tailY));
			}
			else if(tailY<tailY2) { //�ؿ� ����
				snake.add(new XY(tailX,tailY-1));
			}
			else if(tailY>tailY2) { //���� ����
				snake.add(new XY(tailX,tailY+1));
			}
		}
		
		// �����迭  { {1,2,3},    GridLayout  1 2 3  LeftTop
		//             {4,5,6},                4 5 6
		// ���� �̵� : �������̵� X+, ���� �̵� Y+  LeftBottom
		boolean checkCollision(int x, int y) {
			if(x<0 || x>19 || y<0 || y>19) { //���� �ε���.
				return true;
			}
			if(map[y][x] == 8) { //��ź�� �浹��.
				return true;
			}
			//���� Body�� �ε����°�?
			for(XY xy : snake) {
				if(x == xy.x && y == xy.y) {
					return true;
				}
			}
			return false;
		}
		void moveSnake() {
			XY headXY = snake.get(0);
			int headX = headXY.x;
			int headY = headXY.y;
			
			if(dir==0) { //up
				//�浹üũ : ���� �ε����°�?
				boolean isColl = checkCollision(headX,headY-1);
				if(isColl==true) {
					labelMessage.setText("�浹! ��������!");
					timer.stop();
					return;
				}
				snake.add(0, new XY(headX,headY-1));
				snake.remove(snake.size()-1);//���� �ڸ�.
				
			} else if(dir==1) { //down
				boolean isColl = checkCollision(headX,headY+1);
				if(isColl==true) {
					labelMessage.setText("�浹! ��������!");
					timer.stop();
					return;
				}
				 
				snake.add(0, new XY(headX,headY+1));
				snake.remove(snake.size()-1);//���� �ڸ�.
				
			} else if(dir==2) { //left
				boolean isColl = checkCollision(headX-1,headY);
				if(isColl==true) {
					labelMessage.setText("�浹! ��������!");
					timer.stop();
					return;
				}
				snake.add(0, new XY(headX-1,headY));
				snake.remove(snake.size()-1);//���� �ڸ�.
				
			} else if(dir==3){ //right
				boolean isColl = checkCollision(headX+1,headY);
				if(isColl==true) {
					labelMessage.setText("�浹! ��������!");
					timer.stop();
					return;
				}
				snake.add(0, new XY(headX+1,headY));
				snake.remove(snake.size()-1);//���� �ڸ�.
				
			}
		}
		
		void makeSnakeList() {
			snake.add(new XY(10,10));//ù����� Head
			snake.add(new XY(9,10)); //
			snake.add(new XY(8,10)); //���� ��� TAIL
		}
		
		void initUI() {
			this.setLayout(new BorderLayout());
			
			panelNorth = new JPanel();
			panelNorth.setPreferredSize(new Dimension(400,100));
			panelNorth.setBackground(Color.BLACK);
			panelNorth.setLayout(new FlowLayout());
			
			labelTitle = new JLabel("����:0�� �ð�:0��");
			labelTitle.setPreferredSize(new Dimension(400,50));
			labelTitle.setFont(new Font("�������", Font.BOLD,
					20));
			labelTitle.setForeground(Color.WHITE);
			labelTitle.setHorizontalAlignment(JLabel.CENTER);
			panelNorth.add(labelTitle);
			
			labelMessage = new JLabel("������ ��������!");
			labelMessage.setPreferredSize(new Dimension(400,20));
			labelMessage.setFont(new Font("�������", Font.BOLD,
					20));
			labelMessage.setForeground(Color.YELLOW);
			labelMessage.setHorizontalAlignment(JLabel.CENTER);
			panelNorth.add(labelMessage);
			
			this.add("North",panelNorth);
			
			panelCenter = new JPanel();
			panelCenter.setLayout(new GridLayout(20,20));
			for(int i=0; i<20; i++) { //�� - ����
				for(int j=0; j<20; j++) { //�� - ����
					map[i][j] = 0; //�ʱ�ȭ
					panels[i][j] = new JPanel();
					panels[i][j].setPreferredSize(new Dimension(20,20));
					panels[i][j].setBackground(Color.GRAY);
					panelCenter.add(panels[i][j]);
				}
			}
			this.add("Center", panelCenter);
			this.pack();
			
		}
	}
	public static void main(String[] args) {
		new MyFrame("������ũ ����");
	}

}
