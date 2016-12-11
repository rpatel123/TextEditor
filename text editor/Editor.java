package editor;

import com.sun.tools.doclets.formats.html.SourceToHTMLConverter;
import com.sun.tools.javac.util.BasicDiagnosticFormatter;
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.VPos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.util.ArrayList;

import java.util.Objects;


public class Editor extends Application {
    private final Rectangle cursor;
    public Editor() {
        cursor = new Rectangle(0, 0);
    }


    private class KeyEventHandler implements EventHandler<KeyEvent> {
        int textCenterX = 5;
        int textCenterY = 0;

        Group rootObj;
        ArrayList<LinkedListDeque.Node> lines = new ArrayList<>(100);
        int lineNum = 0;
        LinkedListDeque textSoFar = new LinkedListDeque();


        public Text displayText = new Text(0, 5, "");
        public int fontSize = 12;
        public double windowWidth;
        public double windowHeight;
        public double leftMargin;
        public double rightMargin;

        private String fontName = "Verdana";


        public KeyEventHandler(final Group root, int windowWidth, int windowHeight) {
            rootObj = root;
            displayText = new Text(textCenterX, textCenterY, "");
            displayText.setTextOrigin(VPos.TOP);
            displayText.setFont(Font.font(fontName, fontSize));
            this.windowWidth = windowWidth;
            this.windowHeight = windowHeight;
            this.leftMargin = 5;
            this.rightMargin = windowWidth - 5;

            rootObj.getChildren().add(displayText);
        }

        @Override
        public void handle(KeyEvent keyEvent) {
            if (keyEvent.getEventType() == KeyEvent.KEY_TYPED && !keyEvent.isShortcutDown() && !keyEvent.isShortcutDown()) {
                String newChar = keyEvent.getCharacter();
                Text newT = new Text(textCenterX, textCenterY, newChar);




                if (newChar.length() > 0 && newChar.charAt(0) != 8) {
                    rootObj.getChildren().add(newT);
                    newT.setTextOrigin(VPos.TOP);
                    textSoFar.addText(newT);
                    keyEvent.consume();
                }
                render(textCenterX, textCenterY);

            } else {
                if (keyEvent.getEventType() == KeyEvent.KEY_PRESSED) {

                    KeyCode code = keyEvent.getCode();
                    if (keyEvent.isShortcutDown()) {
                        if (code == KeyCode.P) {
                            System.out.println("XPos: " + textCenterX + " YPos: " +textCenterY);
                        }
                        if (code == KeyCode.PLUS || code == KeyCode.EQUALS) {
                            fontSize += 4;
                            displayText.setFont(Font.font(fontName, fontSize));
                        } else if (code == KeyCode.MINUS) {
                            fontSize = Math.max(0, fontSize - 4);
                            displayText.setFont(Font.font(fontName, fontSize));
                        }
                        render(textCenterX, textCenterY);
                    } else if (code == KeyCode.BACK_SPACE) {
                        Text oldLast = textSoFar.removeText();
                        cursor.setX(cursor.getX() - (int) Math.round(oldLast.getLayoutBounds().getWidth()));
                        rootObj.getChildren().remove(oldLast);
                        if (oldLast == lines.get(lineNum).item) {
                            lines.set(lineNum, null);
                            lineNum -= 1;
                        }
                    } else if (code == KeyCode.LEFT) {
                        textSoFar.moveLeft();
                    } else if (code == KeyCode.RIGHT) {
                        textSoFar.moveRight();
                    }
                    render(textCenterX, textCenterY);
                }
            }
        }


        private void render(double textCenterX, double textCenterY) {

            cursor.setWidth(1.0);

            rootObj.getChildren().clear();

            LinkedListDeque.Node newFront = textSoFar.sentinel;

            while (newFront.next != null) {
                Text curr = newFront.next.item;
                curr.setX(textCenterX);
                curr.setY(textCenterY);
                curr.setFont(Font.font(fontName, fontSize));
                if (curr.getX() + curr.getLayoutBounds().getWidth() > windowWidth - 5 || curr.getText().equals("\r")) {
                    if (curr.getText().equals("\r")) {
                        textCenterY += (int) Math.round(curr.getLayoutBounds().getHeight()/2);
                        cursor.setY(textCenterY);
                    } else {
                        textCenterY += (int) Math.round(curr.getLayoutBounds().getHeight());
                        cursor.setY(textCenterY);
                    }
                    textCenterX = 5;
                    cursor.setX(textCenterX);
                } else {
                    textCenterX += curr.getLayoutBounds().getWidth();
                    cursor.setX(curr.getX() + curr.getLayoutBounds().getWidth());
                }
                cursor.setHeight(curr.getLayoutBounds().getHeight());
                displayText.toFront();
                rootObj.getChildren().add(curr);
                newFront = newFront.next;
            }

            Text newChar = textSoFar.cursor.item;
            if (newChar != null) {
                cursor.setX(newChar.getX() + newChar.getLayoutBounds().getWidth());
                cursor.setY(textCenterY);
            }
            System.out.println("Bounding box: " + cursor);

            rootObj.getChildren().add(cursor);
        }

    }




    private class RectangleBlinkEventHandler implements EventHandler<ActionEvent> {
        private int currentColorIndex = 0;
        private Color[] boxColors =
                {Color.BLACK, Color.TRANSPARENT};



        private void changeColor() {
            cursor.setFill(boxColors[currentColorIndex]);
            currentColorIndex = (currentColorIndex + 1) % boxColors.length;
        }

        @Override
        public void handle(ActionEvent event) {changeColor();}
    }
    public void makeRectangleColorChange() {
        final Timeline timeline = new Timeline();
        timeline.setCycleCount(Timeline.INDEFINITE);
        RectangleBlinkEventHandler cursorChange = new RectangleBlinkEventHandler();
        KeyFrame keyFrame = new KeyFrame(Duration.seconds(.5), cursorChange);
        timeline.getKeyFrames().add(keyFrame);
        timeline.play();
    }

    /** Makes the text bounding box change color periodically. */

    @Override
    public void start(Stage primaryStage) {
        Group root = new Group();

        int windowWidth = 500;
        int windowHeight = 500;
        Scene scene = new Scene(root, windowWidth, windowHeight, Color.WHITE);


        EventHandler<KeyEvent> keyEventHandler =
                new KeyEventHandler(root, windowWidth, windowHeight);
        scene.setOnKeyTyped(keyEventHandler);
        scene.setOnKeyPressed(keyEventHandler);
//        root.getChildren().add(cursor);
        makeRectangleColorChange();
        primaryStage.setTitle("Text Editor");

        primaryStage.setScene(scene);
        primaryStage.show();


    }



    public static void main(String[] args) {
        launch(args);
    }

}






