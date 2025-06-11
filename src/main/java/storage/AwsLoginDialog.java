package storage;

import java.awt.Frame;
import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JTextField;

/**
 * AwsLoginDialog provides a simple login form for entering AWS credentials and S3 bucket details.
 * This dialog collects user input such as Access Key, Secret Key, Region, and Bucket Name,
 * which are required to authenticate and interact with AWS S3 services from the desktop application.
 */
public class AwsLoginDialog extends JDialog {

    // Input field for AWS Access Key ID
    private JTextField accessKeyField;

    // Input field for AWS Secret Access Key
    private JTextField secretKeyField;

    // Input field for AWS Region (default set to "us-east-1")
    private JTextField regionField;

    // Input field for S3 Bucket Name
    private JTextField bucketNameField;

    // Flag to indicate whether the login was submitted successfully
    private boolean submitted = false;

    /**
     * Constructor initializes the dialog with labeled input fields for AWS credentials
     * and a "Connect" button. It also centers the dialog on the screen.
     *
     * @param parent the parent frame from which this dialog is launched
     */
    public AwsLoginDialog(Frame parent) {
        // Initialize a modal dialog with title
        super(parent, "AWS Login", true);
        setSize(400, 300);
        setLocationRelativeTo(parent);
        setLayout(new GridLayout(5, 2, 10, 10)); // 5 rows, 2 columns with spacing

        // Create text fields for user input
        accessKeyField = new JTextField();
        secretKeyField = new JTextField();
        regionField = new JTextField("us-east-1"); // Default region
        bucketNameField = new JTextField();

        // Add labeled fields to the dialog
        add(new JLabel("AWS Access Key:"));
        add(accessKeyField);
        add(new JLabel("AWS Secret Key:"));
        add(secretKeyField);
        add(new JLabel("Region:"));
        add(regionField);
        add(new JLabel("S3 Bucket Name:"));
        add(bucketNameField);

        // Create a button to submit the credentials
        JButton loginBtn = new JButton("Connect");

        // When the button is clicked, mark as submitted and close the dialog
        loginBtn.addActionListener(e -> {
            submitted = true;
            setVisible(false);
        });

        // Empty label to align button layout properly
        add(new JLabel());
        add(loginBtn);
    }

    /**
     * Indicates whether the user submitted the dialog successfully.
     *
     * @return true if submitted; false otherwise
     */
    public boolean isSubmitted() {
        return submitted;
    }

    /**
     * @return the AWS Access Key entered by the user
     */
    public String getAccessKey() {
        return accessKeyField.getText().trim();
    }

    /**
     * @return the AWS Secret Key entered by the user
     */
    public String getSecretKey() {
        return secretKeyField.getText().trim();
    }

    /**
     * @return the AWS region entered by the user
     */
    public String getRegion() {
        return regionField.getText().trim();
    }

    /**
     * @return the S3 bucket name entered by the user
     */
    public String getBucketName() {
        return bucketNameField.getText().trim();
    }
}
