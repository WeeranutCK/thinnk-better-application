package cs211.project.controllers;

public class NumberInputDialogController extends TextInputDialogController {
    @Override
    public boolean checkInput() {
        if (inputText.isEmpty()) {
            setErrorLabel("Input text cannot be empty.");
            return false;
        }
        try {
            double inputNumber = Double.parseDouble(inputText);
            if (inputNumber <= 0.0) {
                setErrorLabel("Amount can't be zero or lower.");
                return false;
            }
        } catch (NumberFormatException e) {
            setErrorLabel("Invalid Number");
            return false;
        }
        return true;
    }
}
