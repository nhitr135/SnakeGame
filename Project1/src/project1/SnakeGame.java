/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package project1;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;
import javax.swing.*;
import java.io.*;

public class SnakeGame extends JPanel implements ActionListener, KeyListener {  
    private double speedMultiplier = 1.0; 

    int boardWidth;
    int boardHeight;
    int tileSize = 30;

    Tile snakeHead;
    LinkedList<Tile> snakeBody = new LinkedList<>();
    LinkedList<Tile> obstacles = new LinkedList<>();

    Tile food;
    Tile pinkFood;
    Tile specialFood;
    Random random;
    
    boolean isSpecialFood = false;
    int specialFoodTimer = 0;
    
    boolean isPinkFood = false;
    int pinkFoodTimer = 0;

    int velocityX;
    int velocityY;
    Timer gameLoop;

    boolean gameOver = false;
    
    private LinkedList<Tile> enemySnakeBody = new LinkedList<>();
private boolean enemySnakeVisible = false;
private int enemySnakeTimer = 0;
private int enemyDirectionX = 0;
private int enemyDirectionY = 0;
    
    private int obstacleTimer;
    private Random obstacleRandom = new Random();
       
    private int score = 0;
    private int highScore = 0;
    private final String highscoreFile = "highscore.txt";

    public SnakeGame(int boardWidth, int boardHeight) {
        this.boardWidth = boardWidth;
        this.boardHeight = boardHeight;
        setPreferredSize(new Dimension(this.boardWidth, this.boardHeight));
        setBackground(Color.black);
        addKeyListener(this);
        setFocusable(true);
        
        loadHighScore();
        initializeGame();
    }

    private void loadHighScore() {
        try (BufferedReader in = new BufferedReader(new FileReader(highscoreFile))) {
            String line = in.readLine();
            if (line != null) {
                highScore = Integer.parseInt(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }        

    private void initializeGame() {       
        snakeHead = new Tile(10, 5);
        snakeBody = new LinkedList<>();
        food = new Tile(15, 10);
        specialFood = null;
        pinkFood = null;
        random = new Random();
        placeFood();
        placePinkFood();
        placeSpecialFood();
        velocityX = 1;
        velocityY = 0;           
        gameOver = false;  
        
        obstacles = new LinkedList<>();
        obstacleTimer = obstacleRandom.nextInt(100) + 30;
        
        speedMultiplier = 1.0; 
        gameLoop = new Timer((int)(250 / speedMultiplier), this);
        gameLoop.start();
        
        score = 0;
        
        enemySnakeBody = new LinkedList<>();
        enemySnakeVisible = false;
        enemySnakeTimer = random.nextInt(200) + 100; 
        enemyDirectionX = 0;
        enemyDirectionY = 0;

    }    

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);          
    }

    public void draw(Graphics g) {
        for(int i = 0; i < boardWidth / tileSize; i++) {
            g.drawLine(i * tileSize, 0, i * tileSize, boardHeight);
            g.drawLine(0, i * tileSize, boardWidth, i * tileSize); 
    }
        if (isPinkFood && pinkFood != null) {
            g.setColor(Color.PINK);
            g.fill3DRect(pinkFood.x * tileSize, pinkFood.y * tileSize, tileSize, tileSize, true);
    }
        g.setColor(Color.red);
        g.fill3DRect(food.x * tileSize, food.y * tileSize, tileSize, tileSize, true);
    
        if (isSpecialFood && specialFood != null) {
            g.setColor(Color.yellow);
            g.fill3DRect(specialFood.x * tileSize, specialFood.y * tileSize, tileSize, tileSize, true);
    }      
        g.setColor(Color.green);
        g.fill3DRect(snakeHead.x * tileSize, snakeHead.y * tileSize, tileSize, tileSize, true);
        
        Node<Tile> current = snakeBody.head;
        while (current != null) {
            Tile snakePart = current.data;
            g.fill3DRect(snakePart.x * tileSize, snakePart.y * tileSize, tileSize, tileSize, true);
            current = current.next;
        }      
        g.setColor(Color.BLUE);
        current = obstacles.head;
        while (current != null) {
        Tile obstacle = current.data;
        g.fill3DRect(obstacle.x * tileSize, obstacle.y * tileSize, tileSize, tileSize, true);
        current = current.next;
    }  
    if (enemySnakeVisible) {
        g.setColor(Color.WHITE);
        current = enemySnakeBody.head;
        while (current != null) {
            Tile part = current.data;
            g.fill3DRect(part.x * tileSize, part.y * tileSize, tileSize, tileSize, true);
            current = current.next;
        }
    }  
    g.setFont(new Font("Arial", Font.PLAIN, 16));
    g.setColor(Color.YELLOW);  
    g.drawString("Score: " + score, tileSize - 16, tileSize);
    g.drawString("High Score: " + highScore, tileSize - 16, tileSize + 20);
    }

    public void placeFood(){
        do {
        food.x = random.nextInt(boardWidth/tileSize);
        food.y = random.nextInt(boardHeight/tileSize);
        } while (isOccupied(food,true) || isOccupied(food, false));
    }
    public void placePinkFood() {
    if (random.nextInt(100) < 40) { 
        do {
            pinkFood = new Tile(random.nextInt(boardWidth / tileSize), random.nextInt(boardHeight / tileSize));
        } while (isOccupied(pinkFood, true) || isOccupied(pinkFood, false));
        
        isPinkFood = true;           
        pinkFoodTimer = 100; 
    } else {
        isPinkFood = false;
        pinkFood = null;
    }
}

    public void applyPowerUp() {
    int powerUpType = random.nextInt(2); 
    if (powerUpType == 0) {
        int shrinkSize = 5 + random.nextInt(3); 
        snakeBody.remove(shrinkSize); 
    } else if (powerUpType == 1) {
        speedMultiplier = 1.0; 
        gameLoop.setDelay((int)(250 / speedMultiplier));
    }
}
    
    public void placeSpecialFood() {
        if (random.nextInt(100) < 20) { 
            do {
                specialFood = new Tile(random.nextInt(boardWidth / tileSize), random.nextInt(boardHeight / tileSize));
            } while (isOccupied(specialFood, true) || isOccupied(specialFood, false));
            
            isSpecialFood = true;           
            specialFoodTimer = 50; 
        } else {
            isSpecialFood = false;
            specialFood = null;
        }
    }

    public void move() {
        Tile previousPosition = new Tile(snakeHead.x, snakeHead.y);
        if (collision(snakeHead, food)) {
            snakeBody.add(new Tile(food.x, food.y));
            placeFood();
            
            score++;
            speedMultiplier += 0.075;
            gameLoop.setDelay((int)(250 / speedMultiplier));
        } else if (isSpecialFood && collision(snakeHead, specialFood)) {
            int growth = 3 + random.nextInt(3); 
            for (int i = 0; i < growth; i++) {
                snakeBody.add(new Tile(specialFood.x, specialFood.y));
            }
            isSpecialFood = false;
            specialFood = null;
            score += growth;
        } else if(isPinkFood && pinkFood != null && collision(snakeHead, pinkFood)) {
            applyPowerUp();
            isPinkFood = false;
            pinkFood = null;}
      
        
        snakeHead.x += velocityX;
        snakeHead.y += velocityY;
    
        Tile currentPosition = previousPosition; 
        Node<Tile> current = snakeBody.head;

        while (current != null) {
            Tile temp = current.data; 
            current.data = currentPosition; 
            currentPosition = temp; 
            current = current.next;
        }
        if (pinkFoodTimer > 0) {
            pinkFoodTimer--;
        } else if (pinkFoodTimer == 0 && !isPinkFood) {
            placePinkFood();
            pinkFoodTimer = random.nextInt(200) + 100; 
        }
               
        if (specialFoodTimer > 0) {
            specialFoodTimer--;
        } else if (specialFoodTimer == 0 && isSpecialFood) {
            isSpecialFood = false;
            specialFood = null;
        }
        for (int i = 0; i < snakeBody.size(); i ++){
            if(collision(snakeHead, snakeBody.get(i))) {
                gameOver = true;
            }
        }
        if (snakeHead.x < 0 || snakeHead.x >= boardWidth / tileSize || 
    snakeHead.y < 0 || snakeHead.y >= boardHeight / tileSize) {
    gameOver = true; 
}

        if (collisionWithObstacles()) {
    gameOver = true; 
}

        
        
        
    }
    
    private void moveEnemySnake() {
    if (!enemySnakeVisible) return;

    Tile head = enemySnakeBody.get(0);
    Tile newHead = new Tile(head.x + enemyDirectionX, head.y + enemyDirectionY);

    for (int i = enemySnakeBody.size() - 1; i > 0; i--) {
        enemySnakeBody.set(i, enemySnakeBody.get(i - 1));
    }
    enemySnakeBody.set(0, newHead);

    if (newHead.x < 0 || newHead.x >= boardWidth / tileSize || 
        newHead.y < 0 || newHead.y >= boardHeight / tileSize) {
        enemySnakeBody.remove(1); 
    }
    if (enemySnakeBody.size() == 0) {
        enemySnakeVisible = false; 
        enemySnakeTimer = random.nextInt(200) + 100; 
    }
}

private void spawnEnemySnake() {
    if (!enemySnakeVisible && enemySnakeTimer <= 0) { 
        int length = 10 + random.nextInt(6); 
        enemySnakeBody = new LinkedList<>();
        int startX = random.nextInt(boardWidth / tileSize);
        int startY = random.nextInt(boardHeight / tileSize);
        int direction = random.nextInt(4); 

        switch (direction) {
            case 0: enemyDirectionX = 1; enemyDirectionY = 0; startX = 0; break; 
            case 1: enemyDirectionX = -1; enemyDirectionY = 0; startX = boardWidth / tileSize - 1; break; 
            case 2: enemyDirectionX = 0; enemyDirectionY = 1; startY = 0; break; 
            case 3: enemyDirectionX = 0; enemyDirectionY = -1; startY = boardHeight / tileSize - 1; break; 
        }

        for (int i = 0; i < length; i++) {
            enemySnakeBody.add(new Tile(startX - i * enemyDirectionX, startY - i * enemyDirectionY));
        }

        enemySnakeVisible = true;
    } else {
        enemySnakeTimer--; 
    }
}

    public boolean collision(Tile tile1, Tile tile2) {
        return tile1.x == tile2.x && tile1.y == tile2.y;
    }
    
    private boolean collisionWithObstacles() {
        Node<Tile> current = obstacles.head; 
        while (current != null) {
            if (collision(snakeHead, current.data)) {
                return true; 
            }
            current = current.next;
        }
        return false;
    }
    
    private boolean collisionWithEnemySnake() {
    Node<Tile> enemyBodyCurrent = enemySnakeBody.head;
    while (enemyBodyCurrent != null) {
        if (collision(snakeHead, enemyBodyCurrent.data)) {
            return true; 
        }
        enemyBodyCurrent = enemyBodyCurrent.next;
    }
    Node<Tile> playerBodyCurrent = snakeBody.head;
    while (playerBodyCurrent != null) {
        enemyBodyCurrent = enemySnakeBody.head;
        while (enemyBodyCurrent != null) {
            if (collision(playerBodyCurrent.data, enemyBodyCurrent.data)) {
                return true; 
            }
            enemyBodyCurrent = enemyBodyCurrent.next;
        }
        playerBodyCurrent = playerBodyCurrent.next;
    }

    return false; 
}


    @Override
    public void actionPerformed(ActionEvent e) { 
        if (gameOver) {
        gameLoop.stop();
        return; 
    }
        move();
        moveEnemySnake();
        spawnEnemySnake();
        repaint();
        
        obstacleTimer--;
    if (obstacleTimer <= 0) {
        addRandomObstaclesBatch(); 
        obstacleTimer = obstacleRandom.nextInt(100) + 30; 
    }   
    if (collisionWithEnemySnake()) {
        gameOver = true; 
    }
    if (gameOver) {
            gameLoop.stop();
            if (score > highScore) {
                highScore = score;
                saveHighScore();  
            }
            int response = JOptionPane.showConfirmDialog(this, "Game Over! Score: " 
                    + score + "\nDo you want to play again?",
                    "Game Over", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE);
            if (response == JOptionPane.YES_OPTION) {
                initializeGame();
            } else {
                System.exit(0);
            }
        }
    }   


   
    private void saveHighScore() {
        try (PrintWriter out = new PrintWriter(new FileWriter(highscoreFile))) {
            out.println(highScore);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

   public void addRandomObstaclesBatch() {
    int numberOfObstacles = 5 + random.nextInt(6); 
    for (int i = 0; i < numberOfObstacles; i++) {
        Tile obstacle;
        do {
            obstacle = new Tile(random.nextInt(boardWidth / tileSize), random.nextInt(boardHeight / tileSize));
        } while (isOccupied(obstacle, true) || isOccupied(obstacle, false));

        obstacles.add(obstacle);
    }
}

    
    private boolean isOccupied(Tile tile, boolean checkEntireSnake) {
    if (checkEntireSnake && collision(tile, snakeHead)) {
        return true;
    }
    Node<Tile> current = snakeBody.head;
    while (current != null) {
        if (collision(tile, current.data)) {
            return true;
        }
        current = current.next;
    }
    if (!checkEntireSnake) {
        return false;
    }
    current = obstacles.head;
    while (current != null) {
        if (collision(tile, current.data)) {
            return true;
        }
        current = current.next;
    }
    return false;
}
		 
	@Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_UP && velocityY != 1) {
            velocityX = 0;
            velocityY = -1;
        }
        else if (e.getKeyCode() == KeyEvent.VK_DOWN && velocityY != -1) {
            velocityX = 0;
            velocityY = 1;
        }
        else if (e.getKeyCode() == KeyEvent.VK_LEFT && velocityX != 1) {
            velocityX = -1;
            velocityY = 0;
        }
        else if (e.getKeyCode() == KeyEvent.VK_RIGHT && velocityX != -1) {
            velocityX = 1;
            velocityY = 0;
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyReleased(KeyEvent e) {}
   
}