package proCollab.projectManagement.capstoneProject.service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.springframework.core.env.Environment;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import proCollab.projectManagement.capstoneProject.model.Task;

@Service
public class EmailService {

    private final JavaMailSender javaMailSender;
    private final Environment env;

    public EmailService(JavaMailSender javaMailSender, Environment env) {
        this.javaMailSender = javaMailSender;
        this.env = env;
    }

    public void sendVerificationEmail(String to, String verificationLink) {
        String baseUrl = env.getProperty("APPLICATION_BASE_URL");
        if (baseUrl == null || baseUrl.isEmpty()) {
            throw new IllegalStateException("APPLICATION_BASE_URL is not configured");
        }
        String emailSubject = "Verify Your Email - ProCollab";
        String emailContent = "<html><body>" +
                "<h2>Welcome to ProCollab!</h2>" +
                "<p>Dear User,</p>" +
                "<p>Thank you for signing up with ProCollab. To activate your account, please verify your email address by clicking on the link below:</p>"
                +
                "<p><a href=\"" + baseUrl + verificationLink + "\">Verify Email</a></p>" +
                "<p>Please note that this link will expire in 24 hours.</p>" +
                "<p>If you have any questions or need assistance, please contact us at <a href=\"mailto:support@procollab.com\">support@procollab.com</a>.</p>"
                +
                "<p>Best regards,</p>" +
                "<p>The ProCollab Team</p>" +
                "</body></html>";

        sendHtmlEmail(to, emailSubject, emailContent);
    }

    private void sendHtmlEmail(String to, String subject, String content) {
        MimeMessage message = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(content, true); // Set content as HTML
            javaMailSender.send(message);
        } catch (MessagingException e) {
            // Handle exception
        }
    }

    public void sendTaskMail(String to, Task task) {
        String baseUrl = env.getProperty("APPLICATION_BASE_URL");
        if (baseUrl == null || baseUrl.isEmpty()) {
            throw new IllegalStateException("APPLICATION_BASE_URL is not configured");
        }

        String emailSubject = "New Task Assigned - Pro-Collab: " + task.getName();
        String emailContent = "<html><body>" +
                "<h2>Hello,</h2>" +
                "<p>You have been assigned a new task:</p>" +
                "<h3>Task Details:</h3>" +
                "<ul>" +
                "<li><strong>Name:</strong> " + task.getName() + "</li>" +
                "<li><strong>Description:</strong> " + task.getDescription() + "</li>" +
                "<li><strong>Date:</strong> " + task.getDate() + "</li>" +
                "<li><strong>Assigned By:</strong> " + task.getCreatorName() + "</li>" +
                "</ul>" +
                "<p>Please log in to your ProCollab account to view the task.</p>" +
                "<p><a href=\"" + baseUrl + "/login\">Login to ProCollab</a></p>" +
                "<p>If you have any questions or need assistance, please contact your project manager.</p>" +
                "<p>Best regards,</p>" +
                "<p>The ProCollab Team</p>" +
                "</body></html>";

        sendHtmlEmail(to, emailSubject, emailContent);
    }

    // Send an email
    public void sendEmail(String to, String subject, String text) {
        sendHtmlEmail(to, subject, text);
    }

    public void sendInviteMail(String email, Long employeeId, String password) {
        String baseUrl = env.getProperty("APPLICATION_BASE_URL");
        if (baseUrl == null || baseUrl.isEmpty()) {
            throw new IllegalStateException("APPLICATION_BASE_URL is not configured");
        }
        String emailSubject = "Welcome to ProCollab";
        String emailContent = "<html><body>" +
                "<h2>Welcome to ProCollab!</h2>" +
                "<p>Dear User,</p>" +
                "<p>You've been added to ProCollab. You can access the app with the below credentials:</p>"
                +
                "<p>EmployeeId:\"" + employeeId + "\"</p>" +
                "<p>Username:\"" + email + "\"</p>" +
                "<p>Password:\"" + password + "\"</p>" +
                "<p>Application Link:\"" + baseUrl + "\"</p>" +
                "<p>If you have any questions or need assistance, please contact us at <a href=\"mailto:support@procollab.com\">support@procollab.com</a>.</p>"
                +
                "<p>Best regards,</p>" +
                "<p>The ProCollab Team</p>" +
                "</body></html>";

        sendHtmlEmail(email, emailSubject, emailContent);
    }
}
