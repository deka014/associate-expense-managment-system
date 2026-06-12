package com.adp.ExpenseService.service;

import com.adp.ExpenseService.entities.Expense;
import com.adp.ExpenseService.entities.Receipt;
import com.adp.ExpenseService.entities.User;
import com.adp.ExpenseService.enums.ExpenseCategory;
import com.adp.ExpenseService.enums.ExpenseStatus;
import com.adp.ExpenseService.enums.SubmitStatus;
import com.adp.ExpenseService.repository.ExpenseRepository;
import com.adp.ExpenseService.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ExpenseService {

    @Autowired
    private ExpenseRepository expenseRepository;

    @Autowired
    private UserServiceClient userServiceClient;

    @Autowired
    private UserRepository userRepository;

    public Expense create(Expense expense) {
        return expenseRepository.save(expense);
    }

    public List<Expense> read() {
        return expenseRepository.findAll();
    }

    public Expense read(Integer id) {
        return expenseRepository.findById(id).orElse(null);
    }

    public Expense update(Expense expense) {
        Expense temp = read(expense.getId());

        if (temp != null) {
            temp = expense;
            expenseRepository.save(temp);
        }

        return temp;
    }

    public Expense delete(Integer id) {
        Expense expense = read(id);

        if (expense != null) {

            expenseRepository.delete(expense);
        }

        return expense;
    }

    public List<Expense> getExpensesByUserId(Integer userId) {
        User user = userRepository.readUserById(userId);
        List<Expense> expenses = new ArrayList<>();

        if (user != null) {
            expenses = expenseRepository.findByUserId(user);
        }

        return expenses;
    }

    public List<Expense> getExpenseByCategoryAndId(String category, Integer id) {
        User user = userServiceClient.readUserById(id);
        return expenseRepository.findExpenseByCategoryAndUserId(ExpenseCategory.valueOf((category)), user);
    }

    public List<Expense> findByExpensesDateBetweenAndUserId(Integer id, String starDate, String endDate) {
        Date fromDate = Date.valueOf(starDate);
        Date toDate = Date.valueOf(endDate);

        User user = userRepository.readUserById(id);

        return expenseRepository.findByExpenseDateBetweenAndUserId(fromDate, toDate, user);

    }

    public String extractFileExtension(String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            return "";
        }
        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex == -1) {
            return "";
        }
        return fileName.substring(lastDotIndex + 1);
    }

    public List<Expense> getExpensesByUserIdAndStatus(Integer userId, ExpenseStatus status) {

        User user = userRepository.readUserById(userId);
        List<Expense> expenses = new ArrayList<>();

        if (user != null) {
            expenses = expenseRepository.findByUserIdAndStatus(user, status);
        }

        if (expenses.size() > 0) {
            return expenses;
        }

        return null;

    }

    public Receipt getReceiptByExpenseId(Integer expenseId) {
        Expense expense = read(expenseId);

        if (expense.getReceipt() != null) {
            return expense.getReceipt();
        }

        return null;
    }

    public Expense submitExpense(Expense expense) {

        expense.setSubmitStatus(SubmitStatus.TRUE);
        return expenseRepository.save(expense);

    }

    public List<Expense> getDraftExpenses(Integer id) {
        List<Expense> drafts = new ArrayList<>();

        User user = userRepository.readUserById(id);

        if(user != null)
            drafts = expenseRepository.findBySubmitStatusAndUserId(SubmitStatus.FALSE, user);

        if(drafts.size() > 0) {
            return drafts;
        }

        return null;
    }

    public List<Expense> getPendingExpensesByMgrId(Integer managerId)
    {
        User mgr = userServiceClient.readUserById(managerId);

        if(mgr == null) return null;

        List<User> users = userServiceClient.retrieveEmployeesByManagerId(managerId);
        List<Expense> pendingExpenses = new ArrayList<>();

        if(users != null)
        {
            for(User user: users) {
                Integer userId = user.getId();

                List<Expense> pendingExpenseOfUser = getExpensesByUserIdAndStatus(userId,
                        ExpenseStatus.PENDING);

                if(pendingExpenseOfUser != null)
                {
                    System.out.println("Pending expense of User with id "+ userId + " found!");
                    for(Expense expense : pendingExpenseOfUser )
                    {
                        if(expense.getSubmitStatus() != SubmitStatus.FALSE)
                        {
                            pendingExpenses.add(expense);
                        }
                    }

                }
            }
        }
        return pendingExpenses;
    }

    public List<Expense> getApprovedExpensesByMgrId(Integer managerId)
    {
        User mgr = userServiceClient.readUserById(managerId);

        if(mgr == null) return null;

        List<User> users = userServiceClient.retrieveEmployeesByManagerId(managerId);
        List<Expense> approvedExpenses = new ArrayList<>();

        if(users != null)
        {
            for(User user: users) {
                Integer userId = user.getId();

                List<Expense> approvedExpenseOfUser = getExpensesByUserIdAndStatus(userId,
                        ExpenseStatus.APPROVED);

                if(approvedExpenseOfUser != null)
                {
                    System.out.println("Approved expense of User with id "+ userId + " found!");
                    for(Expense expense : approvedExpenseOfUser )
                    {
                        if(expense.getSubmitStatus() != SubmitStatus.FALSE)
                        {
                            approvedExpenses.add(expense);
                        }
                    }
                }
            }
        }
        return approvedExpenses;
    }

    public List<Expense> getRejectedExpensesByMgrId(Integer managerId)
    {
        User mgr = userServiceClient.readUserById(managerId);

        if(mgr == null) return null;

        List<User> users = userServiceClient.retrieveEmployeesByManagerId(managerId);
        List<Expense> rejectedExpenses = new ArrayList<>();

        if(users != null)
        {
            for(User user: users) {
                Integer userId = user.getId();

                List<Expense> rejectedExpenseOfUser = getExpensesByUserIdAndStatus(userId,
                        ExpenseStatus.REJECTED);

                if(rejectedExpenseOfUser != null)
                {
                    System.out.println("rejcted expense of User with id "+ userId + " found!");
                    for(Expense expense : rejectedExpenseOfUser )
                    {
                        if(expense.getSubmitStatus() != SubmitStatus.FALSE)
                        {
                            rejectedExpenses.add(expense);
                        }
                    }
                }
            }
        }
        return rejectedExpenses;
    }

    public List<Expense> getAllExpensesByMgrId(Integer managerId)
    {
        User mgr = userRepository.readUserById(managerId);

        if(mgr == null) return null;

        List<User> users = userRepository.findUsersByManagerId(managerId);
        List<Expense> allExpenses = new ArrayList<>();

        if(users != null)
        {
            for(User user: users) {
                Integer userId = user.getId();

                List<Expense> allExpenseOfUser = getExpensesByUserId(userId);

                if(allExpenseOfUser != null)
                {
                    System.out.println("All expense of User with id "+ userId + " found!");
                    for(Expense expense : allExpenseOfUser )
                    {
                        if(expense.getSubmitStatus() != SubmitStatus.FALSE)
                        {
                            allExpenses.add(expense);
                        }
                    }
                }
            }
        }
        return allExpenses;
    }

    public boolean isUserCreatingOwnExpense(Authentication authentication, Integer id){
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String authenticatedUserName = userDetails.getUsername();
        System.out.println(authenticatedUserName);
        Optional<User> authenticatedUser = userRepository.findByEmail(authenticatedUserName);
        Integer authenticatedUserId = authenticatedUser.get().getId();
        return authenticatedUserId.equals(id);
    }

    public boolean isManagerAccessingOwnEmployees(Authentication authentication, Integer userId){
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String authenticatedUserName = userDetails.getUsername();
        System.out.println(authenticatedUserName);
        Optional<User> authenticatedUser = userRepository.findByEmail(authenticatedUserName);
        Integer authenticatedUserId = authenticatedUser.get().getId();
        Optional<Integer> managerId = userRepository.findManagerId(userId);
        return authenticatedUserId.equals(managerId.get());
    }

    public boolean isUserAccessingOwnExpenses(Authentication authentication, Integer expenseId)
    {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String authenticatedUserName = userDetails.getUsername();
        System.out.println(authenticatedUserName);
        Optional<User> authenticatedUser = userRepository.findByEmail(authenticatedUserName);
        Integer authenticatedUserId = authenticatedUser.get().getId();
        Optional<Expense> expense = expenseRepository.findById(expenseId);
        User userId = expense.get().getUserId();
        return authenticatedUserId.equals(userId.getId());

    }
//    public boolean isManagerAccessingDashboard(Authentication authentication, Integer managerId){
//        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
//        String authenticatedUserName = userDetails.getUsername();
//        System.out.println(authenticatedUserName);
//        Optional<User> authenticatedUser = userRepository.findByEmail(authenticatedUserName);
//        Integer authenticatedUserId = authenticatedUser.get().getId();
//        List<User> users = userRepository.findByManagerId(managerId);
//        return !users.isEmpty();
//    }
    public boolean isManagerAccessingDashboard(Authentication authentication, Integer managerId){
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String authenticatedUserName = userDetails.getUsername();
        System.out.println(authenticatedUserName);
        Optional<User> authenticatedUser = userRepository.findByEmail(authenticatedUserName);
        Integer authenticatedUserId = authenticatedUser.get().getId();
        return authenticatedUserId.equals(managerId);
    }
    
    public boolean isManagerAccessingOtherEmployees(Authentication authentication, Integer expenseId){
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String authenticatedUserName = userDetails.getUsername();
        System.out.println(authenticatedUserName);
        Optional<User> authenticatedUser = userRepository.findByEmail(authenticatedUserName);
        Integer authenticatedUserId = authenticatedUser.get().getId();
        List<User> users = userRepository.findByManagerId(authenticatedUserId);
        Optional<Expense> expense = expenseRepository.findById(expenseId);
        User userId = expense.get().getUserId();
        for(User user:users){
            if(user.getId().equals(userId.getId())) return true;
        }
        return false;
    }
    
}