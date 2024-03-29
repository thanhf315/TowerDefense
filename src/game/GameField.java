package game;

import javax.swing.*;
import java.awt.*;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

public class GameField implements Runnable{
    private PathPoints line1, line2, line3, line4;
    private GamePanel gamePanel;
    private GameStage gameStage;
    private long lastTime;
    private boolean placingMachineGunTower;
    private Tower newMachineGunTower;
    private boolean placingNormalTower;
    private Tower newNormalTower;
    private boolean placingSniperTower;
    private Tower newSniperTower;
    private double elapsedTime;
    private boolean gameIsOver;
    private boolean gameIsWon;
    public int lives;
    public int Reward;
    public int frameCounter;
    public  int killsCounter;

    List<Enemy> enemies;
    List<Tower> towers;
    List<Bullet> bullets;

    public int[][] Map = {
            {0, 0 ,0 ,0 ,1 ,0 ,0 ,0 ,0 ,1 ,1 ,1 ,1 ,0},
            {0, 0 ,0 ,0 ,1 ,0 ,0 ,0 ,0 ,0 ,0 ,1 ,0 ,0},
            {0, 0 ,1 ,1 ,1 ,0 ,0 ,0 ,0 ,1 ,1 ,1 ,1 ,0},
            {0, 0 ,1 ,0 ,0 ,1 ,0 ,1 ,1 ,0 ,0 ,0 ,0 ,0},
            {1, 0 ,1 ,0 ,0 ,0 ,0 ,0 ,0 ,0 ,1 ,0 ,0 ,1},
            {0, 0 ,0 ,1 ,1 ,0 ,1 ,0 ,0 ,0 ,1 ,1 ,1 ,0},
            {0, 0 ,0 ,0 ,0 ,0 ,1 ,0 ,0 ,0 ,0 ,0 ,0 ,0},
            {1, 1 ,0 ,0 ,1 ,1 ,1 ,0 ,0 ,0 ,0 ,0 ,0 ,0},
    };

    public GameField() {
        gameStage = GameStage.SETUP;
        gamePanel = new GamePanel(this);
        Thread t = new Thread(this);
        t.start();
    }

    public void run () {
        while (true) {
            if (gameStage == GameStage.SETUP) {
                doSetupStuff();
            } else if (gameStage == GameStage.UPDATE) {
                doUpdateTasks();
            } else if (gameStage == GameStage.DRAW) {
                gamePanel.repaint();
                try { Thread.sleep(5); } catch (Exception e) {}
            } else if (gameStage == GameStage.WAIT) {
                try { Thread.sleep(100); } catch (Exception e) {}
                gameStage = GameStage.UPDATE;
            } else if (gameStage == GameStage.END) {}
        }
    }

    private void doSetupStuff () {
        JFrame f = new JFrame();
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setTitle("Tower Defense");
        f.setContentPane(gamePanel);
        f.pack();
        f.setVisible(true);

        Reward = 600;
        lives = 10;
        killsCounter =0;
        frameCounter = 0;
        lastTime = System.currentTimeMillis();


        ClassLoader myLoader = this.getClass().getClassLoader();
        InputStream normalline = myLoader.getResourceAsStream("line/normalline.txt");
        Scanner s1 = new Scanner (normalline);
        line1 = new PathPoints(s1);
        InputStream tankerline = myLoader.getResourceAsStream("line/tankerline.txt");
        Scanner s2 = new Scanner (tankerline);
        line2 = new PathPoints(s2);
        InputStream smallline = myLoader.getResourceAsStream("line/smallline.txt");
        Scanner s3 = new Scanner (smallline);
        line3 = new PathPoints(s3);
        InputStream bossline = myLoader.getResourceAsStream("line/bossline.txt");
        Scanner s4 = new Scanner (bossline);
        line4 = new PathPoints(s4);

        enemies = new LinkedList<Enemy>();
        towers = new LinkedList<Tower>();
        bullets = new LinkedList<Bullet>();

        placingMachineGunTower = false;
        newMachineGunTower = null;
        placingNormalTower = false;
        newNormalTower = null;
        placingSniperTower = false;
        newSniperTower = null;
        gameIsOver = false;
        gameIsWon = false;
        gameStage = GameStage.UPDATE;
    }

    private void doUpdateTasks() {
        if (gameIsOver) {
            gameStage = GameStage.DRAW;
            return;
        }
        if (gameIsWon) {
            gameStage = GameStage.DRAW;
            return;
        }

        long currentTime = System.currentTimeMillis();
        elapsedTime = ((currentTime - lastTime) / 1000.0);
        lastTime = currentTime;

        for (Tower t : new LinkedList<Tower>(towers)) {
            t.interact(this, elapsedTime);
        }
        for (Bullet b : new LinkedList<Bullet>(bullets)) {
            b.updatepos(elapsedTime);
            b.updateTTL(elapsedTime);
            b.interact(this, elapsedTime, b);
            if (b.isDone())
                bullets.remove(b);
        }
        for (Enemy e : new LinkedList<Enemy>(enemies)) {
            e.updatepos();
            if (e.getPosition().isAtTheEnd()) {
                enemies.remove(e);
                lives--;
            }
        }

        this.generateEnemies();
        this.placeMachineGunTower();
        this.placeNormalTower();
        this.placeSniperTowers();

        frameCounter++;

        if (lives <= 0) {
            gameIsOver = true;
            lives = 0;
        }

        if (killsCounter >= 300) {
            gameIsWon = true;
        }
        gameStage = GameStage.DRAW;
    }

    public void draw(Graphics g) {
        if (gameStage != GameStage.DRAW)
            return;

        new DrawMap(g);
        g.setColor(Color.BLACK);
        g.drawString("Lives: "  + lives, 1450, 75);
        g.drawString("Cost: " + Reward, 1450, 100);

        for(Tower t: new LinkedList<Tower>(towers))
            t.draw(g);
        for(Enemy e: new LinkedList<Enemy>(enemies))
            e.draw(g);
        for(Bullet b: new LinkedList<Bullet>(bullets))
            b.draw(g);

        if(newMachineGunTower != null)
            newMachineGunTower.draw(g);
        if(newNormalTower != null)
            newNormalTower.draw(g);
        if(newSniperTower != null)
            newSniperTower.draw(g);

        ImageLoader loader = ImageLoader.getLoader();
        Image gameover = loader.getImage("resources/game_over.png");
        Image gamewin = loader.getImage("resources/game_win.png");
        if(lives <= 0)
            g.drawImage(gameover, 0, 0, null);

        if(killsCounter >= 500) {
            g.drawImage(gamewin, 0, 0, null);
        }
        gameStage = GameStage.WAIT;
    }

    public void generateEnemies() {
        if(frameCounter % 10 == 0 && frameCounter<50) {
            enemies.add(new NormalEnemy(line1.getStart()));
        }
        else if(frameCounter % 30 == 0 && frameCounter >= 50) {
            enemies.add(new NormalEnemy(line1.getStart()));

        }
        else if(frameCounter % 50 == 0 && frameCounter >= 100) {
            enemies.add(new NormalEnemy(line1.getStart()));
            enemies.add(new SmallerEnemy(line3.getStart()));
        }
        else if(frameCounter % 100 == 0 && frameCounter >= 150) {
            enemies.add(new NormalEnemy(line1.getStart()));
            enemies.add(new SmallerEnemy(line3.getStart()));
        }
        else if(frameCounter % 200 == 0 && frameCounter >= 200) {
            enemies.add(new NormalEnemy(line1.getStart()));
            enemies.add(new TankerEnemy(line2.getStart()));
            enemies.add(new SmallerEnemy(line3.getStart()));
        }
        else if(frameCounter % 300 == 0 && frameCounter >= 250) {
            enemies.add(new NormalEnemy(line1.getStart()));
            enemies.add(new TankerEnemy(line2.getStart()));
            enemies.add(new SmallerEnemy(line3.getStart()));
            enemies.add(new BossEnemy(line4.getStart()));
        }
    }

    public void placeSniperTowers() {
        Coordinate mouseLocation = new Coordinate(gamePanel.mouseX, gamePanel.mouseY);
        int x = (int) (mouseLocation.x/100);
        int y = (int) (mouseLocation.y/100);
        if(gamePanel.mouseX > 1500 && gamePanel.mouseX < 1600 &&
                gamePanel.mouseY > 350 && gamePanel.mouseY < 450 &&
                gamePanel.mouseIsPressed && Reward >= 300) {

            placingSniperTower = true;
            newSniperTower = new SniperTower(mouseLocation);
        }
        else if(gamePanel.mouseX > 0 && gamePanel.mouseX < 1400 &&
                gamePanel.mouseY > 0 && gamePanel.mouseY < 800 &&
                gamePanel.mouseIsPressed && placingSniperTower && Map[y][x]==1) {

            mouseLocation.x = (int) (mouseLocation.x / 100) * 100;
            mouseLocation.y = (int) (mouseLocation.y / 100) * 100;
            newSniperTower.setPosition(mouseLocation);
            towers.add(new SniperTower(mouseLocation));
            Reward -= 300;
            newSniperTower = null;
            placingSniperTower = false;
            Map[y][x] = 0;
        }
        else if(gamePanel.mouseX > 0 && gamePanel.mouseX < 1400 &&
                gamePanel.mouseY > 0 && gamePanel.mouseY < 800 &&
                gamePanel.mouseIsPressed && placingSniperTower && Map[y][x]==0) {
            newSniperTower = null;
            placingSniperTower = false;
        }
        if(newSniperTower != null)
        {
            mouseLocation.x -= 50;
            mouseLocation.y -= 50;
            newSniperTower.setPosition(mouseLocation);
        }
    }

    public void placeMachineGunTower() {
        Coordinate mouseLocation = new Coordinate(gamePanel.mouseX, gamePanel.mouseY);
        int x = (int) (mouseLocation.x / 100);
        int y = (int) (mouseLocation.y / 100);
        if(gamePanel.mouseX > 1500 && gamePanel.mouseX < 1600 &&
                gamePanel.mouseY > 500 && gamePanel.mouseY < 600 &&
                gamePanel.mouseIsPressed && Reward >= 200) {

            placingMachineGunTower= true;
            newMachineGunTower= new MachineGunTower(mouseLocation);
        }
        else if(gamePanel.mouseX > 0 && gamePanel.mouseX < 1400 &&
                gamePanel.mouseY > 0 && gamePanel.mouseY < 800 &&
                gamePanel.mouseIsPressed && placingMachineGunTower && Map[y][x] == 1) {

            mouseLocation.x = (int) (mouseLocation.x / 100) * 100;
            mouseLocation.y = (int) (mouseLocation.y / 100) * 100;
            newMachineGunTower.setPosition(mouseLocation);
            towers.add(new MachineGunTower(mouseLocation));
            Reward -= 200;
            newMachineGunTower = null;
            placingMachineGunTower = false;
            Map[y][x] = 0;
        }
        else if(gamePanel.mouseX > 0 && gamePanel.mouseX < 1400 &&
                gamePanel.mouseY > 0 && gamePanel.mouseY < 800 &&
                gamePanel.mouseIsPressed && placingMachineGunTower && Map[y][x]==0) {
            newMachineGunTower = null;
            placingMachineGunTower = false;
        }
        if (newMachineGunTower != null) {
            mouseLocation.x -= 50;
            mouseLocation.y -= 50;
            newMachineGunTower.setPosition(mouseLocation);
        }

    }


    public void placeNormalTower() {
        Coordinate mouseLocation = new Coordinate(gamePanel.mouseX, gamePanel.mouseY);
        int x = (int) (mouseLocation.x/100);
        int y = (int) (mouseLocation.y/100);
        if(gamePanel.mouseX > 1500 && gamePanel.mouseX < 1600 &&
                gamePanel.mouseY > 650 && gamePanel.mouseY < 750 &&
                gamePanel.mouseIsPressed && Reward >= 100) {

            placingNormalTower = true;
            newNormalTower = new NormalTower(mouseLocation);
        }
        else if(gamePanel.mouseX > 0 && gamePanel.mouseX < 1400 &&
                gamePanel.mouseY > 0 && gamePanel.mouseY < 800 &&
                gamePanel.mouseIsPressed && placingNormalTower && Map[y][x]==1) {

            mouseLocation.x = (int) (mouseLocation.x/100)*100;
            mouseLocation.y = (int) (mouseLocation.y/100)*100;
            newNormalTower.setPosition(mouseLocation);
            towers.add(new NormalTower(mouseLocation));
            Reward -= 100;
            newNormalTower = null;
            placingNormalTower = false;
            Map[y][x] = 0;
        }
        else if(gamePanel.mouseX > 0 && gamePanel.mouseX < 1400 &&
                gamePanel.mouseY > 0 && gamePanel.mouseY < 800 &&
                gamePanel.mouseIsPressed && placingNormalTower && Map[y][x]==0) {
            newNormalTower = null;
            placingNormalTower = false;
        }
        if(newNormalTower != null)
        {
            mouseLocation.x -= 50;
            mouseLocation.y -= 50;
            newNormalTower.setPosition(mouseLocation);
        }
    }
    public void updatehealth(Enemy e, Bullet b) {
        e.health -= (b.strength-e.armor);
    }
}


