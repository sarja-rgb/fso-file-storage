package storage;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import util.AwsS3Util;

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
        super(parent, "AWS Login", true);
        init(parent);
    }

    private void init(Frame parent){
        setSize(400, 250);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(10,10));

        // Top panel for labels and text fields
        JPanel inputPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        AwsS3Credential awsS3Credential = new  AwsS3Credential();
        try {
             awsS3Credential = AwsS3Util.loadCredential();
        } catch (Exception ex) {
          System.out.println("Error loading credentials");
        }
        accessKeyField = new JTextField(awsS3Credential.getAccessKey());
        secretKeyField = new JTextField(awsS3Credential.getSecretKey());
        regionField = new JTextField("us-east-1");
        bucketNameField = new JTextField(awsS3Credential.getBucketName());

        inputPanel.add(new JLabel("AWS Access Key:"));
        inputPanel.add(accessKeyField);
        inputPanel.add(new JLabel("AWS Secret Key:"));
        inputPanel.add(secretKeyField);
        inputPanel.add(new JLabel("Region:"));
        inputPanel.add(regionField);
        inputPanel.add(new JLabel("S3 Bucket Name:"));
        inputPanel.add(bucketNameField);

        // Bottom panel for buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton cancelBtn = new JButton("Cancel");
        JButton connectBtn = new JButton("Connect");

        cancelBtn.addActionListener(e -> {
            submitted = false;
            setVisible(false);
        });

        connectBtn.addActionListener(e -> {
            submitted = true;
            setVisible(false);
        });

        buttonPanel.add(cancelBtn);
        buttonPanel.add(connectBtn);

        add(inputPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
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
