package com.shopwell.api.service.jobs;

import com.shopwell.api.model.entity.Customer;
import com.shopwell.api.service.CustomerService;
import com.shopwell.api.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.quartz.Job;
import org.quartz.JobExecutionContext;

import java.time.LocalDate;
import java.util.List;

@RequiredArgsConstructor
public class BirthdayEmailJob implements Job {
    private final EmailService emailService;

    private final CustomerService customerService;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) {
        LocalDate currentDate = LocalDate.now();

        List<Customer> customers = customerService
                .findCustomersByBirthDate(currentDate.getMonthValue(), currentDate.getDayOfMonth());

        customers.forEach(customer -> {
            String customerEmail = customer.getCustomerEmail();
            String subject = "Happy birthday";
            String text = String.format("Dear %s , \n\nWishing you a very happy birthday from all of us at ShopWell", customer.getCustomerFirstName());

            emailService.sendEmail(customerEmail, subject, text);
        });
    }
}
