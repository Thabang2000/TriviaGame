package com.example.demo1;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;

import java.util.Timer;
import java.util.TimerTask;

public class LesothoTriviaGame extends Application {

    private String[] questions = {
            "Which cultural village is known for its traditional Basotho huts?",
            "Which dam is the largest reservoir in Lesotho?",
            "Which river forms the border between Lesotho and South Africa?",
            "Which village hosts the annual Maletsunyane Braai Festival?",
            "What is the highest peak in Lesotho?",
            "Which waterfall is known for its 'smoking' appearance?"
    };
    private String[] answers = {"Ha Kome Cave Houses", "Katse Dam", "Caledon River", "Semonkong", "Thabana Ntlenyana", "Maletsunyane Falls"};
    private String[][] options = {
            {"Ha Kome Cave Houses", "Bokong Cultural Village", "Morija Cultural Village", "Semonkong Cultural Village"},
            {"Katse Dam", "Mohale Dam", "Muela Dam", "Metolong Dam"},
            {"Orange River", "Caledon River", "Senqu River", "Mohokare River"},
            {"Semonkong", "Thaba Bosiu", "Mokhotlong", "Qacha's Nek"},
            {"Thabana Ntlenyana", "Mont-aux-Sources", "Mount Qiloane", "Sani Pass"},
            {"Maletsunyane Falls", "Katse Falls", "Molimo Nthuse Falls", "Thaba-Bosiu Falls"}
    };
    private String[] imagePaths = {"images/ha_kome.jpeg", "images/katse_dam.jpeg", "images/caledon_river.jpeg",
            "images/semonkong.jpeg", "images/thabana_ntlenyana.jpeg", "images/maletsunyane_falls.jpeg"};

    // Paths to the audio files
    private String[] audioPaths = {
            "audio/lesiba.mp3", "audio/lesiba.mp3", "audio/alesiba.mp3",
            "audio/lesiba.mp3", "audio/lesiba.mp3", "audio/lesiba.mp3"
    };

    private String[] videoPaths = {
            "videos/ha_kome.mp4",
            "videos/katse_dam.mp4",
            "videos/caledon_river.mp4",
            "videos/maletsunyane_falls.mp4",
            "videos/semonkongg.mp4",
            "videos/thabana_ntlenyana.mp4"
    };

    private ImageView imageView;
    private Button[] optionButtons;
    private VBox optionsBox;
    private Label questionLabel;
    private TextArea scoreArea;
    private Label timeLabel;
    private BorderPane root; // Add root variable

    private Timer timer;
    private int timeRemaining = 15;
    private int score = 0;
    private int currentQuestionIndex = 0;

    private Stage primaryStage;
    private MediaPlayer mediaPlayer;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        root = new BorderPane(); // Initialize root
        showStartScreen(root); // Pass root to showStartScreen method
        Scene scene = new Scene(root, 800, 700); // Increase window size
        scene.getStylesheets().add(getClass().getResource("styles.css").toExternalForm());
        primaryStage.setTitle("Lesotho Trivia Game");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void showStartScreen(BorderPane root) {
        root.setStyle("-fx-background-color: #dce6f2;");

        VBox centerBox = new VBox(20);
        centerBox.setAlignment(Pos.CENTER);
        root.setCenter(centerBox);

        Label titleLabel = new Label("Welcome to Lesotho Trivia Game");
        titleLabel.setStyle("-fx-font-size: 30px; -fx-font-weight: bold;");
        centerBox.getChildren().add(titleLabel);

        Button startButton = new Button("Start Game");
        startButton.setOnAction(e -> startGame());
        centerBox.getChildren().add(startButton);

        for (int i = 0; i < questions.length; i++) {
            int questionIndex = i;
            Button videoButton = new Button("Watch Video Clip for Question " + (i + 1));
            videoButton.setOnAction(e -> showVideoClip(questionIndex));
            centerBox.getChildren().add(videoButton); // Add button to show video clip
        }

        // Add animation for showing the start screen
        FadeTransition fadeTransition = new FadeTransition(Duration.seconds(1), centerBox);
        fadeTransition.setFromValue(0);
        fadeTransition.setToValue(1);
        fadeTransition.play();
    }

    private void showVideoClip(int questionIndex) {
        String videoPath = videoPaths[questionIndex]; // Fetch the video path using question index
        Media media = new Media(getClass().getResource(videoPath).toExternalForm());
        MediaPlayer mediaPlayer = new MediaPlayer(media);
        MediaView mediaView = new MediaView(mediaPlayer);

        // Create a new stage to display the video clip
        Stage videoStage = new Stage();
        BorderPane root = new BorderPane();
        root.setCenter(mediaView);
        Scene scene = new Scene(root, 600, 400);
        videoStage.setScene(scene);
        videoStage.setTitle("Video Clip");
        videoStage.show();

        mediaPlayer.play();
    }

    private void startGame() {
        imageView = new ImageView();
        imageView.setFitWidth(600);
        imageView.setFitHeight(400);
        root.setTop(imageView);

        VBox centerBox = new VBox(20);
        centerBox.setAlignment(Pos.CENTER);
        root.setCenter(centerBox);

        questionLabel = new Label();
        questionLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
        centerBox.getChildren().add(questionLabel);

        optionsBox = new VBox(10);
        optionsBox.setAlignment(Pos.CENTER);
        centerBox.getChildren().add(optionsBox);

        optionButtons = new Button[4];
        for (int i = 0; i < 4; i++) {
            Button optionButton = new Button();
            optionButton.setPrefWidth(300);
            int finalI = i;
            optionButton.setOnAction(e -> handleAnswer(finalI));
            optionButtons[i] = optionButton;
            optionsBox.getChildren().add(optionButton);
        }

        scoreArea = new TextArea();
        scoreArea.setEditable(false);
        scoreArea.setWrapText(true);
        scoreArea.setPrefSize(600, 100);
        centerBox.getChildren().add(scoreArea);

        timeLabel = new Label();
        timeLabel.setStyle("-fx-font-size: 20px;");
        root.setBottom(timeLabel);

        currentQuestionIndex = 0; // Set the current question index to 0
        loadQuestion(currentQuestionIndex); // Load the first question
        startTimer();

        // Add animation for showing the game screen
        FadeTransition fadeTransition = new FadeTransition(Duration.seconds(1), centerBox);
        fadeTransition.setFromValue(0);
        fadeTransition.setToValue(1);
        fadeTransition.play();
    }

    private void handleAnswer(int selectedOptionIndex) {
        timer.cancel();
        boolean isCorrect = answers[currentQuestionIndex].equals(options[currentQuestionIndex][selectedOptionIndex]);
        if (isCorrect) {
            score++;
            updateScoreArea("Correct! Your score is now: " + score, true);
        } else {
            updateScoreArea("Incorrect. The correct answer is: " + answers[currentQuestionIndex], false);
        }
        navigateNext();
    }

    private void navigateNext() {
        if (currentQuestionIndex < questions.length - 1) {
            currentQuestionIndex++;
            loadQuestion(currentQuestionIndex);
            resetTimer();
            startTimer();
        } else {
            showEndScreen();
            timer.cancel();
        }
    }

    private void updateUI(Runnable runnable) {
        Platform.runLater(runnable);
    }

    private void updateScoreArea(String message, boolean isCorrect) {
        updateUI(() -> {
            scoreArea.setText(message);
            if (isCorrect) {
                scoreArea.getStyleClass().add("correct-answer");
                scoreArea.getStyleClass().remove("incorrect-answer");
            } else {
                scoreArea.getStyleClass().add("incorrect-answer");
                scoreArea.getStyleClass().remove("correct-answer");
            }
        });
    }

    private void loadQuestion(int questionIndex) {
        updateUI(() -> {
            questionLabel.setText(questions[questionIndex]);
            imageView.setImage(new Image(getClass().getResourceAsStream(imagePaths[questionIndex])));
            playAudio(questionIndex); // Play audio for the current question
            for (int i = 0; i < optionButtons.length; i++) {
                optionButtons[i].setText(options[questionIndex][i]);
            }
        });

        // Add animation for loading a question
        TranslateTransition translateTransition = new TranslateTransition(Duration.seconds(0.5), questionLabel);
        translateTransition.setFromY(-100);
        translateTransition.setToY(0);
        translateTransition.play();
    }

    private void startTimer() {
        timeRemaining = 15;
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (timeRemaining > 0) {
                    updateUI(() -> timeLabel.setText("Time remaining: " + timeRemaining + " seconds"));
                    timeRemaining--;
                } else {
                    timer.cancel();
                    updateScoreArea("Time's up! The correct answer is: " + answers[currentQuestionIndex], false);
                    navigateNext();
                }
            }
        }, 0, 1000);
    }

    private void resetTimer() {
        timer.cancel();
        startTimer();
    }

    private void showEndScreen() {
        VBox centerBox = new VBox(20);
        centerBox.setAlignment(Pos.CENTER);
        root.setCenter(centerBox);

        Label titleLabel = new Label("Game Over!");
        titleLabel.setStyle("-fx-font-size: 30px; -fx-font-weight: bold;");
        centerBox.getChildren().add(titleLabel);

        Label scoreLabel = new Label("Your final score is: " + score);
        scoreLabel.setStyle("-fx-font-size: 20px;");
        centerBox.getChildren().add(scoreLabel);

        Button startNewGameButton = new Button("Start New Game");
        startNewGameButton.setOnAction(e -> {
            restartGame(); // Restart the game
        });
        centerBox.getChildren().add(startNewGameButton);

        Button exitButton = new Button("Exit");
        exitButton.setOnAction(e -> Platform.exit());
        centerBox.getChildren().add(exitButton);

        // Add animation for showing the end screen
        FadeTransition fadeTransition = new FadeTransition(Duration.seconds(1), centerBox);
        fadeTransition.setFromValue(0);
        fadeTransition.setToValue(1);
        fadeTransition.play();
    }

    private void restartGame() {
        // Reset all game variables to their initial state
        currentQuestionIndex = 0;
        score = 0;
        scoreArea.setText("");
        loadQuestion(currentQuestionIndex);
        startTimer();
    }

    private void playAudio(int questionIndex) {
        Media media = new Media(getClass().getResource(audioPaths[questionIndex]).toExternalForm());
        mediaPlayer = new MediaPlayer(media);
        mediaPlayer.play();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
