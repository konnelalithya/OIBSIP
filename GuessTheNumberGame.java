import javax.swing.JOptionPane;
import java.util.Random;

public class GuessTheNumberGame {

    public static void main(String[] args) {
        int totalRounds = 3; // You can increase the number of rounds
        int score = 0;

        JOptionPane.showMessageDialog(null, "🎮 Welcome to Guess the Number Game!\nYou have to guess a number between 1 and 100.");

        for (int round = 1; round <= totalRounds; round++) {
            int randomNumber = new Random().nextInt(100) + 1;
            int attempts = 0;
            int maxAttempts = 5;
            boolean isCorrect = false;

            JOptionPane.showMessageDialog(null, "🔁 Round " + round + " - You have " + maxAttempts + " attempts!");

            while (attempts < maxAttempts) {
                String input = JOptionPane.showInputDialog("Enter your guess (1 to 100):");

                if (input == null) {
                    JOptionPane.showMessageDialog(null, "Game cancelled. ❌");
                    return;
                }

                int guess;
                try {
                    guess = Integer.parseInt(input);
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(null, "❗ Please enter a valid number.");
                    continue;
                }

                attempts++;

                if (guess == randomNumber) {
                    int points = 10 - (attempts - 1) * 2;
                    points = Math.max(points, 1); // Minimum 1 point
                    score += points;

                    JOptionPane.showMessageDialog(null, "🎉 Correct! You guessed it in " + attempts + " attempts.\nYou earned " + points + " points.");
                    isCorrect = true;
                    break;
                } else if (guess < randomNumber) {
                    JOptionPane.showMessageDialog(null, "🔻 Too low! Try again.");
                } else {
                    JOptionPane.showMessageDialog(null, "🔺 Too high! Try again.");
                }
            }

            if (!isCorrect) {
                JOptionPane.showMessageDialog(null, "❌ You've used all attempts! The number was: " + randomNumber);
            }
        }

        JOptionPane.showMessageDialog(null, "🏁 Game Over! Your total score: " + score);
    }
}
