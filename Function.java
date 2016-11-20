import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public class Function {
	// I/O
	private ArrayList<Record> records;

	private int numberRecords;
	private int numberInputs;
	private int numberOutputs;

	// Hidden
	private int numberHidden;
	private int numberIterations;
	private double learningRate;

	// Neuron value lists
	private double[] input;
	private double[] hidden;
	private double[] output;

	private double[] errorHidden;
	private double[] errorOut;

	private double[] thetaHidden;
	private double[] thetaOut;

	// Weights
	private double[][] inputWeights; // from input to hidden layer
	private double[][] outputWeights; // from hidden to output layer

	// For Normalizing Data
	private double[] inputMin;
	private double[] inputMax;
	private double[] outputMin;
	private double[] outputMax;

	public Function() {
		/*** ZERO PARAMETERS ***/
		this.numberRecords = this.numberHidden = this.numberInputs = this.numberIterations = 0;
		this.learningRate = this.numberOutputs = 0;

		/*** CLEAR NEURON LISTS VALUES ***/
		this.records = null;
		this.input = null;
		this.hidden = null;
		this.output = null;
		this.errorHidden = null;
		this.errorOut = null;
		this.thetaHidden = null;
		this.thetaOut = null;
		this.thetaOut = null;
		this.inputWeights = null;
		this.outputWeights = null;
	}

	public void loadTrainingData(String filename) throws IOException {
		Scanner in = new Scanner(new File(filename));
		
		this.numberRecords = in.nextInt();
		this.numberInputs = in.nextInt();
		this.numberOutputs = in.nextInt();
		
		this.records = new ArrayList<Record>();
		
		for(int i = 0; i < this.numberRecords; i++) {
			double[] inputValues = new double[this.numberInputs];
			for(int j = 0; j < this.numberInputs; j++) {
				inputValues[j] = in.nextDouble(); //load value from file
			}
			
			double[] outputValues = new double[this.numberOutputs];
			for(int j = 0; j < this.numberOutputs; j++) {
				outputValues[j] = in.nextDouble(); //load output value from file
			}
			
			Record record = new Record(inputValues, outputValues);
			
			this.records.add(record);
		}
		in.close();
	}

	public void setParameters(int m, int it, double r, int s) {
		this.numberHidden = m;
		this.numberIterations = it;
		this.learningRate = r;

		Random random = new Random(s);

		this.input = new double[this.numberInputs];
		this.hidden = new double[this.numberHidden];
		this.output = new double[this.numberOutputs];

		this.errorHidden = new double[this.numberHidden];
		this.errorOut = new double[this.numberOutputs];

		this.thetaHidden = new double[this.numberHidden];

		for (int i = 0; i < this.numberHidden; i++) {
			this.thetaHidden[i] = 2*random.nextDouble() - 1; //between [-1,1]
		}
		
		this.thetaOut = new double[this.numberOutputs];
		for (int i = 0; i < this.numberOutputs; i++) {
			this.thetaHidden[i] = 2*random.nextDouble() - 1; //between [-1,1]
		}

		this.inputWeights = new double[this.numberInputs][this.numberHidden];
		for(int i = 0; i < this.numberInputs; i++){
			for(int j = 0; j < this.numberHidden; j++){
				this.inputWeights[i][j] = 2*random.nextDouble() - 1; //between [-1,1]
			}
		}
		this.outputWeights = new double[this.numberHidden][this.numberOutputs];
		for(int i = 0; i < this.numberHidden; i++){
			for(int j = 0; j < this.numberOutputs; j++){
				this.outputWeights[i][j] = 2*random.nextDouble() - 1; //between [-1,1]

			}
		}
	
	}

	public void train() {
		scaleTrainingData();
		for(int i = 0; i < this.numberIterations; i++) { //for each iteration
			for(int j = 0; j < this.numberRecords; j++) { //for each training datum
				forwardCalculation(this.records.get(j).input); //do a forward pass
				backwardCalculation(this.records.get(j).output); //do a backward pass
			}
		}
	}

	private void forwardCalculation(double[] values) {
		/***   INPUT LAYER   ***/
		for(int i = 0; i < this.numberInputs; i++) {
			this.input[i] = values[i]; //input values to the network
		}
		
		/***   HIDDEN LAYER   ***/
		for(int i = 0; i < this.numberHidden; i++) {
			double sum = 0;
			
			for(int j = 0; j < this.numberInputs; j++) {
				sum += this.input[j] * inputWeights[j][i]; //sum the weights*the input together
			}
			sum += thetaHidden[i]; //add this node's theta value
			
			this.hidden[i] = 1/(1 + Math.exp(-sum)); //pass value to hidden layer
		}
		
		/***   OUTPUT LAYER   ***/
		for(int i = 0; i < this.numberOutputs; i++) {
			double sum = 0;
			for(int j = 0; j < this.numberHidden; j++) {
				sum += this.hidden[i] * this.outputWeights[j][i]; //sum th weights*the input
			}
			sum += thetaOut[i]; //add this nodes theta
			this.output[i] = 1/(1 + Math.exp(-sum)); //padd the value to the output layer
		}
	}

	private void backwardCalculation(double[] values){
		/***   COMPUTING ERRORS   ***/
		
		/****    OUTPUT LAYER    ****/
		for(int i = 0; i < this.numberOutputs; i++){
			this.errorOut[i] = this.output[i] * (1 - this.output[i]) * (values[i] - this.output[i]);
		}
		
		/****    HIDDEN LAYER    ****/
		for(int i = 0; i < this.numberHidden; i++) {
			double sum = 0;
			for(int j = 0; j < this.numberOutputs; j++) {
				sum += this.outputWeights[i][j] * this.errorOut[j];
			}
			this.errorHidden[i] = this.hidden[i] * (1 - this.hidden[i]) * sum;
		}
		
		/***   UPDATE WEIGHTS   ***/
		
		/****    OUTPUT LAYER    ****/
		for(int i = 0; i < this.numberHidden; i++) {
			for(int j = 0; j < this.numberOutputs; j++){
				this.outputWeights[i][j] += this.learningRate * this.hidden[i] * this.errorOut[j];
			}
		}
		
		/****    INPUT LAYER    ****/
		for(int i = 0; i < this.numberInputs; i++) {
			for(int j = 0; j < this.numberHidden; j++) {
				this.inputWeights[i][j] += this.learningRate * this.hidden[i] * this.errorHidden[j];
			}
		}
		
		/***   UPDATE THETAS   ***/
		
		/****    OUTPUT LAYER    ****/
		for(int i = 0; i < this.numberOutputs; i++) {
			this.thetaOut[i] += this.learningRate * this.errorOut[i];
		}
		/****    HIDDEN LAYER    ****/
		for(int i = 0; i < this.numberHidden; i++) {
			this.thetaHidden[i] += this.learningRate * this.errorHidden[i];
		}
	}

	private double[] test(double[] input) {
		forwardCalculation(input);
		return this.output; //a reference to the output
	}

	public void testData(String inFile, String outFile) throws IOException {
		Scanner in = new Scanner(new File(inFile));
		PrintWriter out = new PrintWriter(new FileWriter(outFile));
		
		int n = in.nextInt(); //get number of records to test
		
		for(int i = 0; i < n; i++) {
			double[] inputValues = new double[this.numberInputs];
			
			for(int j = 0; j < this.numberInputs; j++) {
				inputValues[j] = (in.nextDouble() - this.inputMin[j]) / (this.inputMax[j] - this.inputMin[j]); //normalize Datum
			}
			
			double[] outputValues = test(inputValues);
			
			for(int j = 0; j < this.numberOutputs; j++) {
				out.print((outputValues[j]*(this.outputMax[j] - this.outputMin[j]) + this.outputMin[j]) + " "); //un-normalize output
			}
			out.println();
		}
		out.close();
		in.close();
	}

	public void validate(String filename) throws IOException {
		Scanner in = new Scanner(new File(filename));
		
		int n = in.nextInt(); //number of records to validate with
		
		double error = 0;
		for(int i = 0; i < n; i++) {
			double[] inputValues = new double[this.numberInputs];
			for(int j = 0; j < this.numberInputs; j++) {
				inputValues[j] = (in.nextDouble() - this.inputMin[j]) / (this.inputMax[j] - this.inputMin[j]); //normalize datum from file
			}
			
			double[] outputValues = new double[this.numberOutputs];
			for(int j = 0; j < this.numberOutputs; j++) {
				outputValues[j] = in.nextDouble(); //get the actual output from the file
			}
			
			double[] predictedOutput = test(inputValues); //feed the input into the function
			
			for(int j = 0; j < this.numberOutputs; j++){
				predictedOutput[j] = predictedOutput[j]*(this.outputMax[j] - this.outputMin[j]) + this.outputMin[j]; //un-normalize output
			}
			
			error += computeError(outputValues, predictedOutput); //compute the error
		}
		
		System.out.println(error / n); //average error
		in.close();
	}

	private double computeError(double[] actual, double[] predicted) {
		double error = 0;
		
		for(int i = 0; i < actual.length; i++) {
			error += Math.pow((actual[i] - predicted[i]), 2); //sum squares of error
		}
		
		return Math.sqrt(error); //return root sum square
	}
	
	private void scaleTrainingData() {
		double[] inputMax = new double[this.numberInputs];
		double[] inputMin = new double[this.numberInputs];
		
		double[] outputMax = new double[this.numberOutputs];
		double[] outputMin = new double[this.numberOutputs];
		
		
		//initial values
		for(int i = 0; i < this.numberInputs; i++) {
			inputMax[i] = Double.MIN_VALUE;
			inputMin[i] = Double.MAX_VALUE;
		}
		for(int i = 0; i < this.numberOutputs; i++) {
			outputMax[i] = Double.MIN_VALUE;
			outputMin[i] = Double.MAX_VALUE;
		}
		
		//get the actual min and max values
		for(int i = 0; i < this.numberRecords; i++) {
			for(int j = 0; j < this.numberInputs; j++) {
				if(inputMax[j] < this.records.get(i).input[j]) {
					inputMax[j] = this.records.get(i).input[j];
				}
				if(inputMin[j] > this.records.get(i).input[j]) {
					inputMin[j] = this.records.get(i).input[j];
				}
			}
			for(int j = 0; j < this.numberOutputs; j++) {
				if(outputMax[j] < this.records.get(i).output[j]) {
					outputMax[j] = this.records.get(i).output[j];
				}
				if(outputMin[j] > this.records.get(i).output[j]) {
					outputMin[j] = this.records.get(i).output[j];
				}
			}
		}
		
		//scale the data (this mutates the data)
		for(int i = 0; i < this.numberRecords; i++) {
			for(int j = 0; j < this.numberInputs; j++) {
				//inputMin[j] = 0;
				//inputMax[j] = 1;
				this.records.get(i).input[j] = (this.records.get(i).input[j] - inputMin[j]) / (inputMax[j] - inputMin[j]);
			}
			
			for(int j = 0; j < this.numberOutputs; j++) {
				//outputMin[j] = 0;
				//outputMax[j] = 1;
				this.records.get(i).output[j] = (this.records.get(i).output[j] - outputMin[j]) / (outputMax[j] - outputMin[j]);
			}
		}
		
		//save the minand max values for validation/testing
		this.inputMin = inputMin;
		this.inputMax = inputMax;
		
		this.outputMin = outputMin;
		this.outputMax = outputMax;
	}

	/*** RECORD CLASS ***/
	private class Record {
		private double[] input;
		private double[] output;

		private Record(double[] in, double[] out) {
			this.input = in;
			this.output = out;
		}
	}
}
