/*
 * Carson Craig
 */
package breakout;

import java.awt.Font;
import java.util.Random;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.newdawn.slick.Color;
import org.newdawn.slick.TrueTypeFont;

public class Breakout {

    public static double paddleLeft, paddleRight, paddleSpeed;
    public static double ballLeft, ballRight, ballTop, ballBottom, ballSpeedX, ballSpeedY, ballAcceleration;
    public static Random r;
    public static int[][] brickBroken = new int[8][100];
    public static double brickTop, brickBottom, transition;
    public static int ballColour = 7, score;
    public static boolean ballActive, gameOn = true;
    public static double[] rowDirection = new double[100];

    public static void main(String[] args) {
        renderGL();

        createBrickArray();

        brickTop = -24;
        brickBottom = -1;

        paddleLeft = 350;
        paddleRight = 450;
        paddleSpeed = 10;

        resetBallPosition();

        Font scoreFont = new Font("Times New Roman", Font.BOLD, 70); //Creates size 40 bold trueTypeLabelFont.
        TrueTypeFont trueTypeScoreFont = new TrueTypeFont(scoreFont, true);



        while (!Display.isCloseRequested()) {
            GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);

            if (gameOn) {
                updatePaddle();
            }

            collisionDetection();
            collisionDetectionBlocks();
            if (gameOn) {
                updateBall();
            }

            Scoreboard();

            GL11.glEnable(GL11.GL_TEXTURE_2D); //Enables textures.
            
            trueTypeScoreFont.drawString(50, 610, "" + score, Color.white); //Displays Player1's trueTypeScoreFont.
            //trueTypeScoreFont.drawString(600, 600, "Word", Color.white);  //Displays Player2's trueTypeScoreFont.
            GL11.glDisable(GL11.GL_TEXTURE_2D); //Disables textures.

            if (gameOn) {
                brickTop = brickTop + 0.05;
                brickBottom = brickBottom + 0.05;
                transition += .3;
            }

            Display.update(); //Updates screen.
            Display.sync(60); //Sets the maximum framerate to 60 fps.
        }
        Display.destroy();
    }

    public static void createBrickArray() {
        int indexColumn = 0;

        while (indexColumn < 56) {

            int indexRow = 0;

            while (indexRow < 8) {

                int maximum;

                maximum = (indexColumn / 9) + 1;

                if (maximum > 6) {
                    maximum = 6;
                }

                r = new Random();
                brickBroken[indexRow][indexColumn] = r.nextInt((maximum - 1) + 1) + 1;
                indexRow++;

                rowDirection[indexColumn] = r.nextDouble()*2 - 1;
            }
            indexColumn++;
        }




        while (indexColumn >= 56 && indexColumn < 100) {

            int indexRow = 0;

            while (indexRow < 8) {

                int minimum = ((indexColumn - 56) / 9) + 1;

                r = new Random();
                brickBroken[indexRow][indexColumn] = r.nextInt((6 - minimum)) + minimum + 1;
                indexRow++;

                rowDirection[indexColumn] = r.nextDouble() - 1.0;
            }
            indexColumn++;
        }


        int indexRow = 0;

        while (indexRow < 8) {

            brickBroken[indexRow][99] = 12;
            indexRow++;
        }
    }

    public static void resetBallPosition() {
        ballLeft = 385;
        ballRight = 415;
        ballTop = 0;
        ballBottom = 30;

        ballAcceleration = 5; 

        ballSpeedY = 5;
    }

    public static void collisionDetection() {

        if (paddleLeft < 0) { //Prevents the Player1's paddle from going through the bottom of the screen.
            paddleLeft = 0;
            paddleRight = 100;
        }
        if (paddleRight > 800) { //Prevents the Player1's paddle from going through the top of the screen.
            paddleLeft = 700;
            paddleRight = 800;
        }


        if (ballBottom > 550 && ((ballLeft < paddleRight && ballLeft > paddleLeft) || (ballRight < paddleRight && ballRight > paddleLeft))) {
            ballSpeedY = -1 * ballSpeedY;
            ballTop--;
            ballBottom--;
            ballColour = 0;
            ballActive = true;
        } else if (ballTop < 0) {
            ballTop = 0;
            ballBottom = 30;
            ballSpeedY = -1 * ballSpeedY;
        } else if (ballBottom > 600) {
            ballSpeedY = -2.5;
        } else if (ballLeft < 0) {
            ballLeft = 0;
            ballRight = 30;
            ballSpeedX = -1 * ballSpeedX;
        } else if (ballRight > 800) {
            ballLeft = 770;
            ballRight = 800;
            ballSpeedX = -1 * ballSpeedX;
        }
    }

    public static void collisionDetectionBlocks() {
        int directionX = 1;
        int directionY = 1;

        int indexColumn = 0;

        while (indexColumn < 100) {

            int indexRow = 0;
            double brickLeft;
            double brickRight;


            brickLeft = 1 + rowDirection[indexColumn] * transition;
            brickRight = 99 + rowDirection[indexColumn] * transition;


            while (indexRow < 8) {

                int y = 0;

                if (rowDirection[indexColumn] >= 0) {

                    while (y < 30) {


                        if (ballTop < brickBottom && ballTop > brickBottom - 5 && brickBroken[indexRow][indexColumn] >= 1 && ((ballLeft < brickRight - 800 * y && ballLeft > brickLeft - 800 * y) || (ballRight < brickRight - 800 * y && ballRight > brickLeft - 800 * y))) {
                            directionY = -1;

                            if (ballSpeedY > 0) {
                                ballSpeedY = ballSpeedY + ballAcceleration;
                            }
                            if (ballSpeedY < 0) {
                                ballSpeedY = ballSpeedY - ballAcceleration;
                            }
                            ballActive = false;
                            score++;
                            ballColour = brickBroken[indexRow][indexColumn];
                            brickBroken[indexRow][indexColumn]--;
                        } else if (ballBottom > brickTop && ballBottom < brickTop + 5 && brickBroken[indexRow][indexColumn] >= 1 && ((ballLeft < brickRight && ballLeft > brickLeft) || (ballRight < brickRight - 800 * y && ballRight > brickLeft - 800 * y))) {
                            directionY = -1;

                            if (ballSpeedY > 0) {
                                ballSpeedY = ballSpeedY + ballAcceleration;
                            }
                            if (ballSpeedY < 0) {
                                ballSpeedY = ballSpeedY - ballAcceleration;
                            }
                            ballActive = false;
                            score++;
                            ballColour = brickBroken[indexRow][indexColumn];
                            brickBroken[indexRow][indexColumn]--;
                        } else if (ballLeft < brickRight - 800 * y && ballLeft > brickRight - 800 * y - 5 && brickBroken[indexRow][indexColumn] >= 1 && ((ballTop < brickBottom && ballTop > brickTop) || (ballBottom < brickBottom && ballBottom > brickTop))) {
                            directionX = -1;

                            if (ballSpeedY > 0) {
                                ballSpeedY = ballSpeedY + ballAcceleration;
                            }
                            if (ballSpeedY < 0) {
                                ballSpeedY = ballSpeedY - ballAcceleration;
                            }
                            ballActive = false;
                            score++;
                            ballColour = brickBroken[indexRow][indexColumn];
                            brickBroken[indexRow][indexColumn]--;
                        } else if (ballRight > brickLeft - 800 * y && ballRight < brickLeft - 800 * y + 5 && brickBroken[indexRow][indexColumn] >= 1 && ((ballTop < brickBottom && ballTop > brickTop) || (ballBottom < brickBottom && ballBottom > brickTop))) {
                            directionX = -1;

                            if (ballSpeedY > 0) {
                                ballSpeedY = ballSpeedY + ballAcceleration;
                            }
                            if (ballSpeedY < 0) {
                                ballSpeedY = ballSpeedY - ballAcceleration;
                            }
                            ballActive = false;
                            score++;
                            ballColour = brickBroken[indexRow][indexColumn];
                            brickBroken[indexRow][indexColumn]--;
                        }  else if (ballTop < brickBottom && ballTop > brickTop && brickBroken[indexRow][indexColumn] >= 1 && ((ballLeft < brickRight - 800 * y && ballLeft > brickLeft - 800 * y) || (ballRight < brickRight - 800 * y && ballRight > brickLeft - 800 * y))) {
                            directionY = -1;

                            if (ballSpeedY > 0) {
                                ballSpeedY = ballSpeedY + ballAcceleration;
                            }
                            if (ballSpeedY < 0) {
                                ballSpeedY = ballSpeedY - ballAcceleration;
                            }
                            ballActive = false;
                            score++;
                            ballColour = brickBroken[indexRow][indexColumn];
                            brickBroken[indexRow][indexColumn]--;
                        }
                        
                        y++;
                    }
                } else {
                    while (y < 30) {

                        if (ballTop < brickBottom && ballTop > brickBottom - 5 && brickBroken[indexRow][indexColumn] >= 1 && ((ballLeft < brickRight + 800 * y && ballLeft > brickLeft + 800 * y) || (ballRight < brickRight + 800 * y && ballRight > brickLeft + 800 * y))) {
                            directionY = -1;

                            if (ballSpeedY > 0) {
                                ballSpeedY = ballSpeedY + ballAcceleration;
                            }
                            if (ballSpeedY < 0) {
                                ballSpeedY = ballSpeedY - ballAcceleration;
                            }
                            ballActive = false;
                            score++;
                            ballColour = brickBroken[indexRow][indexColumn];
                            brickBroken[indexRow][indexColumn]--;
                        } else if (ballBottom > brickTop && ballBottom < brickTop + 5 && brickBroken[indexRow][indexColumn] >= 1 && ((ballLeft < brickRight + 800 * y && ballLeft > brickLeft + 800 * y) || (ballRight < brickRight + 800 * y && ballRight > brickLeft + 800 * y))) {
                            directionY = -1;

                            if (ballSpeedY > 0) {
                                ballSpeedY = ballSpeedY + ballAcceleration;
                            }
                            if (ballSpeedY < 0) {
                                ballSpeedY = ballSpeedY - ballAcceleration;
                            }
                            ballActive = false;
                            score++;
                            ballColour = brickBroken[indexRow][indexColumn];
                            brickBroken[indexRow][indexColumn]--;
                        } else if (ballLeft < brickRight + 800 * y && ballLeft > brickRight + 800 * y - 5 && brickBroken[indexRow][indexColumn] >= 1 && ((ballTop < brickBottom && ballTop > brickTop) || (ballBottom < brickBottom && ballBottom > brickTop))) {
                            directionX = -1;

                            if (ballSpeedY > 0) {
                                ballSpeedY = ballSpeedY + ballAcceleration;
                            }
                            if (ballSpeedY < 0) {
                                ballSpeedY = ballSpeedY - ballAcceleration;
                            }
                            ballActive = false;
                            score++;
                            ballColour = brickBroken[indexRow][indexColumn];
                            brickBroken[indexRow][indexColumn]--;
                        } else if (ballRight > brickLeft + 800 * y && ballRight < brickLeft + 800 * y + 5 && brickBroken[indexRow][indexColumn] >= 1 && ((ballTop < brickBottom && ballTop > brickTop) || (ballBottom < brickBottom && ballBottom > brickTop))) {
                            directionX = -1;

                            if (ballSpeedY > 0) {
                                ballSpeedY = ballSpeedY + ballAcceleration;
                            }
                            if (ballSpeedY < 0) {
                                ballSpeedY = ballSpeedY - ballAcceleration;
                            }
                            ballActive = false;
                            score++;
                            ballColour = brickBroken[indexRow][indexColumn];
                            brickBroken[indexRow][indexColumn]--;
                        }  else if (ballTop < brickBottom && ballTop > brickTop && brickBroken[indexRow][indexColumn] >= 1 && ((ballLeft < brickRight - 800 * y && ballLeft > brickLeft - 800 * y) || (ballRight < brickRight - 800 * y && ballRight > brickLeft - 800 * y))) {
                            directionY = -1;

                            if (ballSpeedY > 0) {
                                ballSpeedY = ballSpeedY + ballAcceleration;
                            }
                            if (ballSpeedY < 0) {
                                ballSpeedY = ballSpeedY - ballAcceleration;
                            }
                            ballActive = false;
                            score++;
                            ballColour = brickBroken[indexRow][indexColumn];
                            brickBroken[indexRow][indexColumn]--;
                        }
                        y++;
                    }
                }




                Colour(brickBroken[indexRow][indexColumn]);

                if (brickBroken[indexRow][indexColumn] >= 1) {

                    int x = 0;

                    if (rowDirection[indexColumn] >= 0) {

                        while (x < 30) {

                            GL11.glBegin(GL11.GL_QUADS);
                            GL11.glVertex2f((int) brickLeft - 800 * x, (int) brickTop);
                            GL11.glVertex2f((int) brickLeft - 800 * x, (int) brickBottom);
                            GL11.glVertex2f((int) brickRight - 800 * x, (int) brickBottom);
                            GL11.glVertex2f((int) brickRight - 800 * x, (int) brickTop);
                            GL11.glEnd();

                            x++;
                        }
                    } else {
                        while (x < 30) {

                            GL11.glBegin(GL11.GL_QUADS);
                            GL11.glVertex2f((int) brickLeft + 800 * x, (int) brickTop);
                            GL11.glVertex2f((int) brickLeft + 800 * x, (int) brickBottom);
                            GL11.glVertex2f((int) brickRight + 800 * x, (int) brickBottom);
                            GL11.glVertex2f((int) brickRight + 800 * x, (int) brickTop);
                            GL11.glEnd();

                            x++;
                        }
                    }

                }


                if (brickBottom >= 600 && brickBroken[indexRow][indexColumn] >= 1) {
                    gameOn = false;
                }


                brickLeft = brickLeft + 100;
                brickRight = brickRight + 100;

                indexRow++;
            }

            brickTop = brickTop - 25;
            brickBottom = brickBottom - 25;
            indexColumn++;

        }

        brickTop = brickTop + 2500;
        brickBottom = brickBottom + 2500;

        ballSpeedX = directionX * ballSpeedX;
        ballSpeedY = directionY * ballSpeedY;

    }

    public static double move(double position, boolean direction, double change) { //Method used for moving the ball and paddles.

        if (direction) { //Determines wether to move the ball up or down.
            return (position + change); //Adds ballSpeed to current position
        }
        return (position - change); //Subtracts ballSpeed from current position
    }

    public static void updateBall() {

        ballLeft = move(ballLeft, true, ballSpeedX);
        ballRight = move(ballRight, true, ballSpeedX);
        ballTop = move(ballTop, true, Math.cbrt(ballSpeedY));
        ballBottom = move(ballBottom, true, Math.cbrt(ballSpeedY));


        Colour(ballColour);


        //Sets Player1's paddle positon.
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glVertex2f((int) ballLeft, (int) ballTop);
        GL11.glVertex2f((int) ballLeft, (int) ballBottom);
        GL11.glVertex2f((int) ballRight, (int) ballBottom);
        GL11.glVertex2f((int) ballRight, (int) ballTop);
        GL11.glEnd();

    }

    public static void Colour(int index) {
        if (index == 0) {
            Color.white.bind();
        }
        if (index == 1) {
            Color.orange.bind();
        }
        if (index == 2) {
            Color.red.bind();
        }
        if (index == 3) {
            Color.magenta.bind();
        }
        if (index == 4) {
            Color.blue.bind();
        }
        if (index == 5) {
            Color.cyan.bind();
        }
        if (index == 6) {
            Color.darkGray.bind();
        }
        if (index >= 7) {
            Color.green.bind();
        }

    }

    public static void updatePaddle() {

        if (Keyboard.isKeyDown(Keyboard.KEY_RIGHT)) { //Moves Player1's paddle upwards.
            paddleLeft = move(paddleLeft, true, paddleSpeed);
            paddleRight = move(paddleRight, true, paddleSpeed);
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_LEFT)) { //Moves Player1's paddle downwards.
            paddleLeft = move(paddleLeft, false, paddleSpeed);
            paddleRight = move(paddleRight, false, paddleSpeed);
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_A)) { //Moves Player1's paddle upwards.
            if (ballSpeedX > -5 && ballActive) {
                ballSpeedX = ballSpeedX - .5;
            }
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_D)) { //Moves Player1's paddle downwards.
            if (ballSpeedX < 5 && ballActive) {
                ballSpeedX = ballSpeedX + .5;
            }
        }

        Color.white.bind();
        //Sets Player1's paddle positon.
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glVertex2f((int) paddleLeft, 550);
        GL11.glVertex2f((int) paddleLeft, 575);
        GL11.glVertex2f((int) paddleRight, 575);
        GL11.glVertex2f((int) paddleRight, 550);
        GL11.glEnd();
    }

    public static void renderGL() {
        try { //Trys to create a game window size 800x700.
            Display.setDisplayMode(new DisplayMode(800, 700));
            Display.create();
        } catch (LWJGLException e) { //Catches exception if game window is not created.
            e.printStackTrace();
            System.exit(0);
        }

        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glShadeModel(GL11.GL_SMOOTH);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glDisable(GL11.GL_LIGHTING);

        GL11.glClearColor(0.0f, 0.0f, 0.0f, 0.0f); //Sets colour to white.
        GL11.glClearDepth(1);

        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        GL11.glViewport(0, 0, 800, 700);
        GL11.glMatrixMode(GL11.GL_MODELVIEW);

        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glLoadIdentity();
        GL11.glOrtho(0, 800, 700, 0, 1, -1);
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
    }

    private static void Scoreboard() {

        Color.white.bind();

        GL11.glBegin(GL11.GL_QUADS);
        GL11.glVertex2f(0, 600);
        GL11.glVertex2f(0, 700);
        GL11.glVertex2f(800, 700);
        GL11.glVertex2f(800, 600);
        GL11.glEnd();

        GL11.glBegin(GL11.GL_QUADS);
        GL11.glVertex2f(0, 0);
        GL11.glVertex2f(0, 5);
        GL11.glVertex2f(800, 5);
        GL11.glVertex2f(800, 0);
        GL11.glEnd();

        GL11.glBegin(GL11.GL_QUADS);
        GL11.glVertex2f(0, 0);
        GL11.glVertex2f(0, 600);
        GL11.glVertex2f(5, 600);
        GL11.glVertex2f(5, 0);
        GL11.glEnd();

        GL11.glBegin(GL11.GL_QUADS);
        GL11.glVertex2f(795, 000);
        GL11.glVertex2f(795, 600);
        GL11.glVertex2f(800, 600);
        GL11.glVertex2f(800, 0);
        GL11.glEnd();

        Color.black.bind();

        GL11.glBegin(GL11.GL_QUADS);
        GL11.glVertex2f(5, 605);
        GL11.glVertex2f(5, 695);
        GL11.glVertex2f(795, 695);
        GL11.glVertex2f(795, 605);
        GL11.glEnd();
    }
}
