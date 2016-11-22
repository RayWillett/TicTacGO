import java.io.IOException;

public class NeuralNetwork_Driver {

	public static void main(String[] args) {
		NeuralNetwork f = new NeuralNetwork();
		
		String trainingFile = ".\\src\\training.txt";
		String validationFile = ".\\src\\validation.txt";
		String testFile = ".\\src\\test.txt";
		String outputFile = ".\\src\\output.txt";
		
		try {
			f.loadTrainingData(trainingFile);
			f.setParameters(9, 100000, 1738, 0.9);
			f.train();
			f.validate(validationFile);
			f.testData(testFile, outputFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
