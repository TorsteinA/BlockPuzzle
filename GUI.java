package com.company;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.Scanner;

/**
 * Created by Torstein on 27.05.2016.
 */
public class GUI  extends JFrame implements ActionListener {

    public JPanel jpGameButtons, jpBottom;
    public ArrayList<JButton> gameButtons;
    public JButton btnStart, btnExit;
    public JMenuBar jMenuBar;
    public JMenu jMenuFile, jMenuAbout;
    public JMenuItem jMenuItemExit, jMenuItemOpenScore, jMenuItemAbout, jMenuItemReset;
    public int gameClickCounter;
    public ArrayList<String> aList;

    public GUI() {
        gameClickCounter = 0;
        aList = new ArrayList<>();
    }

    public void setup() {
        jpGameButtons = new JPanel( new GridLayout( 4, 4, 3, 3 ));
        jpBottom = new JPanel( new GridLayout( 1, 2 ));
        gameButtons = new ArrayList<>();
        btnStart = new JButton( "Reset" );
        btnExit = new JButton( "Exit" );

        setTitle("Block Puzzle");
        setLayout(new BorderLayout());
        add( jpGameButtons, BorderLayout.CENTER );
        add( jpBottom, BorderLayout.SOUTH );

        fillGame();
        jMenuSetup();

        jpBottom.add( btnStart );
        jpBottom.add(btnExit);

        btnStart.addActionListener( this );
        btnExit.addActionListener( this );

        setSize( 250, 300 );
        setLocationRelativeTo(null);
        setVisible( true );
        setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );

        startPrint();
    }

    private void jMenuSetup() {
        jMenuBar = new JMenuBar();
        setJMenuBar(jMenuBar);


        jMenuFile = new JMenu("File");
        jMenuBar.add(jMenuFile);

        jMenuItemOpenScore = new JMenuItem("Open Scores");
        jMenuItemOpenScore.addActionListener(this);
        jMenuFile.add(jMenuItemOpenScore);

        jMenuItemReset = new JMenuItem("Reset");
        jMenuItemReset.addActionListener(this);
        jMenuFile.add(jMenuItemReset);

        jMenuFile.addSeparator();

        jMenuItemExit = new JMenuItem("Exit");
        jMenuItemExit.addActionListener(this);
        jMenuFile.add(jMenuItemExit);


        jMenuAbout = new JMenu("About");
        jMenuBar.add(jMenuAbout);

        jMenuItemAbout = new JMenuItem("About");
        jMenuItemAbout.addActionListener(this);
        jMenuAbout.add(jMenuItemAbout);
    }

    public void fillGame(){
        gameButtons.clear();
        makeGameButtons();
        shuffle();
        clearPanel();
        buttonsToPanel();
        insertBlankButton();
        gameClickCounter = 0;
    }

    public void switchButtons(JButton target){
        int blank = findButtonByText("");
        int targetIndex = findButtonByText(target.getText());
        Collections.swap(gameButtons, targetIndex, blank);
        clearPanel();
        buttonsToPanel();
        gameClickCounter++;
        //System.out.println(gameClickCounter);

        if ( winConditionCheck() ){
            winPrint();
        }

    }

    private void startPrint(){
        JOptionPane.showMessageDialog(this,
                        "Try to line up the buttons from 1 to 15 \nin as few moves as possible");
    }

    private void winPrint() {
        int option = JOptionPane.showConfirmDialog(this,
                        "You Win!! " +
                        "\nScore: " + gameClickCounter +
                        "\n\nDo you want to save result?", null, JOptionPane.YES_NO_OPTION);

        if ( option == JOptionPane.YES_OPTION){
            String name = JOptionPane.showInputDialog(this, "Name?");

            //TODO read file first and add to it if it exists?
            readFile("score.txt");
            printScore(name);
        }
    }

    public void readFile(String fileName) {
        try {

            File file = new File(fileName);

            Scanner input = new Scanner(file);

            aList.clear();

            while (input.hasNext()) {
                //System.out.println(input.nextLine());
                aList.add(input.nextLine() + "\n");
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void printScore(String name){
        try {
            File file = new File("score.txt");
            PrintStream out = new PrintStream(file);
            String outString =  "=========" +
                                "\nName: " + name +
                                "\nScore: " + gameClickCounter +
                                "\n=========";
            //out.print(outString);
            aList.add(outString);

            for(int i = 0; i < aList.size(); i++){
                //System.out.println(aList.get(i));
                out.print(aList.get(i));
            }

            out.close();
            JOptionPane.showMessageDialog(this, "Saved in score.txt");

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
        }
    }

    public void actionPerformed(ActionEvent e) {
        Object clicked = e.getSource();

        if (clicked.getClass() == JMenuItem.class){
            JMenuItem item = (JMenuItem) e.getSource();
            if (item == jMenuItemExit){
                System.exit(0);
            } else if (item == jMenuItemOpenScore){
                openScores();
            } else if (item == jMenuItemAbout){
                showAbout();
            } else if (item == jMenuItemReset){
                fillGame();
            }

        } else if (clicked.getClass() == JButton.class){
            JButton button = (JButton) e.getSource();
            if (button == btnExit) {
                System.exit(0);
            } else if (button == btnStart){
                fillGame();
            } else{
                switchButtons(button);
            }
        }
    }

    private void showAbout() {
        JOptionPane.showMessageDialog(this, "Created my Torstein Alvern in May 2016");
    }

    private void openScores() {
        String message = "";
        readFile("score.txt");
        for (String s : aList){
            message += s;
        }
        JOptionPane.showMessageDialog(this, message);
    }

    private void shuffle(){
        //shuffle array
        long seed = System.nanoTime();
        Collections.shuffle(gameButtons, new Random(seed));

    }

    private void makeGameButtons(){
        for(int i = 0; i < 15; i++){
            JButton jbtn = new JButton(""+ (i + 1));
            jbtn.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 20));
            gameButtons.add(i, jbtn);
            jbtn.addActionListener(this);
        }
    }

    private void clearPanel(){
        jpGameButtons.removeAll();
        jpGameButtons.updateUI();
    }

    private void buttonsToPanel(){
        for(JButton jb : gameButtons){
            jpGameButtons.add(jb);
        }
    }

    private void insertBlankButton(){
        gameButtons.add(new JButton(""));
        jpGameButtons.add(gameButtons.get(15));
    }

    private int findButtonByText(String text){
        for (JButton jb : gameButtons){
            if(jb.getText().equals(text)) return gameButtons.indexOf(jb);
        }
        return -1;
    }

    private boolean winConditionCheck(){
        int correct = 0;

        for(int i = 0; i < gameButtons.size(); i++){
            if (gameButtons.get(i).getText().equals(Integer.toString(i + 1))){
                correct++;
            } else {
                correct--;
            }
        }

        if (correct >= 14){
            return true;
        }
        return false;
    }

}