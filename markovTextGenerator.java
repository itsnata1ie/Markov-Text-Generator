
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class markovTextGenerator {

    public static void main(String[] args) {

        String filePath = "moby_dick_ascii.txt";

        try {
            // Read the file
            String text = Files.readString(Path.of(filePath));
            char[] buffer = text.toCharArray();

            // Count character-to-character transitions
            int[][] transitionCounts = new int[256][256];

            for (int i = 0; i < buffer.length - 1; i += 1) {
                int prevChar = buffer[i];
                int currentChar = buffer[i + 1];

                if (prevChar < 256 && currentChar < 256) {
                    transitionCounts[prevChar][currentChar]++;
                }
            }

            // Convert transition counts into probabilities
            double[][] transitionProbabilities = new double[256][256];

            for (int i = 0; i < 256; i += 1) {
                int rowTotal = 0;

                for (int j = 0; j < 256; j += 1) {
                    rowTotal += transitionCounts[i][j];
                }

                if (rowTotal > 0) {
                    for (int j = 0; j < 256; j += 1) {
                        transitionProbabilities[i][j] = (double) transitionCounts[i][j] / rowTotal;
                    }
                }
            }

            // Generate additional text
            int n = 500;

            char[] output = generate(buffer, n, transitionProbabilities);

            // Print the newly generated text
            System.out.println(new String(output, buffer.length, output.length - buffer.length));

        } catch (IOException e) {
            System.err.println("An error occurred: " + e.getMessage());
        }
    }

    public static char[] generate(char[] buffer, int n, double[][] transition) {
        char lastChar = buffer[buffer.length - 1];

        char[] output = new char[buffer.length + n];

        for (int i = 0; i < buffer.length; i += 1) {
            output[i] = buffer[i];
        }

        for (int step = 0; step < n; step += 1) {
            double thresh = 0;
            double sample = Math.random();
            double[] lookup = transition[lastChar];

            boolean done = false;

            for (int i = 0; i < lookup.length && !done; i += 1) {
                thresh += lookup[i];

                if (sample < thresh) {
                    output[buffer.length + step] = (char) i;
                    lastChar = (char) i;
                    done = true;
                }
            }

            if (!done) {
                output[buffer.length + step] = ' ';
                lastChar = ' ';
            }
        }

        return output;
    }
}