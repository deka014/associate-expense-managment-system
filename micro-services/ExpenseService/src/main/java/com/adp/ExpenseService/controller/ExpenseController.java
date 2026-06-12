package com.adp.ExpenseService.controller;


import com.adp.ExpenseService.entities.Expense;
import com.adp.ExpenseService.entities.Receipt;
import com.adp.ExpenseService.entities.User;
import com.adp.ExpenseService.enums.ExpenseStatus;
import com.adp.ExpenseService.enums.SubmitStatus;
import com.adp.ExpenseService.dtos.ExpenseDTO;
import com.adp.ExpenseService.repository.ExpenseRepository;
import com.adp.ExpenseService.repository.ReceiptRepository;
import com.adp.ExpenseService.repository.UserRepository;
import com.adp.ExpenseService.service.ExpenseService;
import com.adp.ExpenseService.service.UserServiceClient;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/expense")
public class ExpenseController {

    @Autowired
    private ExpenseService expenseService;


    @Autowired
    private UserServiceClient userServiceClient;
    @Autowired
    private ExpenseRepository expenseRepository;

    @Autowired
    private ReceiptRepository receiptRepository;
    
    @Autowired
    private UserRepository userRepository;


//    @PreAuthorize("hasAnyAuthority('EMPLOYEE','MANAGER') and @expenseService.isUserCreatingOwnExpense(authentication,#expenseDTO.userId)")
    @PostMapping
    public ResponseEntity<Expense> createExpense(@RequestBody ExpenseDTO expenseDTO) {
        System.out.println(expenseDTO);
        User user = userRepository.readUserById(expenseDTO.getUserId());
        if (user == null) return ResponseEntity.notFound().build();
        if (expenseDTO.getAmount() == null || expenseDTO.getExpenseDate() == null)
            return ResponseEntity.notFound().build();
        Expense expense = new Expense();
        expense.setUserId(user);
        expense.setCategory(expenseDTO.getCategory());
        expense.setExpenseDate(expenseDTO.getExpenseDate());
        expense.setSubmitStatus(SubmitStatus.FALSE);
        expense.setDescription(expenseDTO.getDescription());
        expense.setAmount(expenseDTO.getAmount());

        Expense expense1 = expenseService.create(expense);
        return ResponseEntity.ok(expense1);
    }


    @PreAuthorize("hasAnyAuthority('EMPLOYEE','MANAGER')and @expenseService.isUserAccessingOwnExpenses(authentication,#expenseId) ")
    @PostMapping("/save/{id}")
    public ResponseEntity<Receipt> createReceipt(@PathVariable("id") Integer expenseId, @RequestParam("file") MultipartFile file) {
        Optional<Expense> expense = expenseRepository.findById(expenseId);
        if (expense.isEmpty()) return ResponseEntity.notFound().build();
        System.out.println(expense.get());
        Receipt receipt = new Receipt();

        try {
            byte[] fileBytes = file.getBytes();
            String base64FIle = Base64.encodeBase64String(fileBytes);
            receipt.setReceiptFile(base64FIle);
        } catch (IOException e) {
            System.out.println("Error Uploading File");
            return ResponseEntity.notFound().build();
        }
        receipt.setExpenseId(expense.get());
        receipt.setFileType(expenseService.extractFileExtension(file.getOriginalFilename()));

        receiptRepository.save(receipt);

        return ResponseEntity.ok(receipt);
    }

    @PreAuthorize("hasAnyAuthority('EMPLOYEE','MANAGER') and @expenseService.isUserAccessingOwnExpenses(authentication,#expenseId)")
    @PutMapping("/save/{id}/{rid}")
    public ResponseEntity<Optional<Receipt>> updateReceipt(@PathVariable("id") Integer expenseId, @PathVariable("rid") Integer receiptId, @RequestParam("file") MultipartFile file) {
        Optional<Expense> expense = expenseRepository.findById(expenseId);
        Optional<Receipt> receipt = receiptRepository.findById(receiptId);
        if (expense.isEmpty() || receipt.isEmpty()) return ResponseEntity.notFound().build();
        System.out.println(expense.get());

        try {
            byte[] fileBytes = file.getBytes();
            String base64FIle = Base64.encodeBase64String(fileBytes);
            receipt.get().setReceiptFile(base64FIle);
        } catch (IOException e) {
            System.out.println("Error Uploading File");
            return ResponseEntity.notFound().build();
        }
        receipt.get().setExpenseId(expense.get());
        receipt.get().setFileType(expenseService.extractFileExtension(file.getOriginalFilename()));

        receiptRepository.save(receipt.get());

        return ResponseEntity.ok(receipt);
    }


    @PreAuthorize("hasAnyAuthority('EMPLOYEE','MANAGER') and @expenseService.isUserAccessingOwnExpenses(authentication,#expenseId)")
    @PutMapping("/submit/{expenseId}")
    public ResponseEntity<Expense> submitExpense(@PathVariable Integer expenseId) {
        Expense expense = expenseService.read(expenseId);
        if (expense == null) return ResponseEntity.notFound().build();

        Expense submittedExpense = expenseService.submitExpense(expense);

        if (submittedExpense != null) {
            return ResponseEntity.ok(submittedExpense);
        }

        return ResponseEntity.notFound().build();
    }

    @PreAuthorize("hasAnyAuthority('MANAGER','EMPLOYEE','ADMIN') and @expenseService.isUserCreatingOwnExpense(authentication,#id)")
    @GetMapping("/{id}")
    public ResponseEntity<List<Expense>> getExpenseByUserId(@PathVariable Integer id) {
        List<Expense> expenses = expenseService.getExpensesByUserId(id);
        if (expenses == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(expenses);

    }

    @PreAuthorize("hasAnyAuthority('MANAGER','EMPLOYEE') and @expenseService.isUserCreatingOwnExpense(authentication,#id)")
    @GetMapping("/{id}/{category}")
    public ResponseEntity<List<Expense>> getExpensesByCategoryAndId(@PathVariable Integer id, @PathVariable String category) {
        List<Expense> expenses = expenseService.getExpenseByCategoryAndId(category, id);
        if (expenses == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(expenses);
    }

    @PreAuthorize("hasAnyAuthority('EMPLOYEE','MANAGER')")
    @GetMapping("/find/{id}")
    public ResponseEntity<List<Expense>> findExpensesBetweenDates(@RequestParam String startDate, @RequestParam String endDate, @PathVariable Integer id) {
        List<Expense> expenses = expenseService.findByExpensesDateBetweenAndUserId(id, startDate, endDate);
        if (expenses == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(expenses);
    }

    @PreAuthorize("hasAnyAuthority('MANAGER','EMPLOYEE') and @expenseService.isUserCreatingOwnExpense(authentication,#userId)")
    @GetMapping("/{id}/status/{status}")
    public ResponseEntity<List<Expense>> retrieveExpenseByUserIdAndStatus(@PathVariable("id") Integer userId, @PathVariable String status) {
        List<Expense> expenses = expenseService.getExpensesByUserIdAndStatus(userId, ExpenseStatus.valueOf(status));
        if (expenses == null) return ResponseEntity.notFound().build();

        List<Expense> result = new ArrayList<>();
        for (Expense expense : expenses) {
            if (expense.getSubmitStatus().equals(SubmitStatus.TRUE)) {
                result.add(expense);
            }
        }

        return ResponseEntity.ok(result);
    }
    
    //Priority
//    @PreAuthorize("hasAuthority('MANAGER') and @expenseService.isManagerAccessingOwnEmployees(authentication,#expense.userId)")
    
    @PreAuthorize("hasAnyAuthority('EMPLOYEE','MANAGER') and @expenseService.isUserCreatingOwnExpense(authentication,#id)")
    @GetMapping("/{id}/status/{status}/dates")
    public ResponseEntity<List<Expense>> expenseByIdStatusAndDates(@PathVariable Integer id,
            @PathVariable String status, @RequestParam String startDate, @RequestParam String endDate) {
        Date fromDate = Date.valueOf(startDate);
        Date toDate = Date.valueOf(endDate);

        User user = userRepository.readUserById(id);
        List<Expense> expenses = expenseService.getExpensesByUserIdAndStatus(id,
                ExpenseStatus.valueOf(status));

        List<Expense> result = new ArrayList<>();

        if (expenses != null) {
            result = expenses.stream().filter(expense -> {
                LocalDate expenseLocalDate = expense.getExpenseDate().toLocalDate();
                LocalDate fromLocalDate = fromDate.toLocalDate();
                LocalDate toLocalDate = toDate.toLocalDate();
                return expenseLocalDate.isAfter(fromLocalDate.minusDays(1))
                        && expenseLocalDate.isBefore(toLocalDate.plusDays(1));
            }).collect(Collectors.toList());
        }

        return ResponseEntity.ok(result);

    }
    @PreAuthorize("hasAuthority('MANAGER')")

    @PutMapping("/updateStatus")
    public ResponseEntity<Expense> updateStatus(@RequestBody Expense expense, @RequestParam("status") ExpenseStatus status) {
        Expense existingExpense = expenseService.read(expense.getId());

        if (existingExpense != null) {
            existingExpense.setStatus(status);
            expenseRepository.save(existingExpense);
            return ResponseEntity.ok(existingExpense);
        } else {
            return ResponseEntity.notFound().build();
        }

    }

    @PreAuthorize("hasAnyAuthority('MANAGER','EMPLOYEE')")   // TODO
    @GetMapping("/receipt/{expenseId}")
    public ResponseEntity<Receipt> retriveReceiptByExpenseId(@PathVariable("expenseId") Integer expenseId) {
        Receipt receipt = expenseService.getReceiptByExpenseId(expenseId);

        if (receipt != null) {
            return ResponseEntity.ok(receipt);
        }

        return ResponseEntity.notFound().build();
    }

    @PreAuthorize("hasAnyAuthority('EMPLOYEE', 'MANAGER') and @expenseService.isUserCreatingOwnExpense(authentication,#id)")
    @GetMapping("/drafts/{id}")
    public ResponseEntity<List<Expense>> retrieveDrafts(@PathVariable("id") Integer id) {
        List<Expense> drafts = expenseService.getDraftExpenses(id);

        if (drafts != null) {
            return ResponseEntity.ok(drafts);
        }

        return ResponseEntity.notFound().build();
    }

//    @PreAuthorize("hasAnyAuthority('MANAGER','EMPLOYEE')") // TODO
//    @GetMapping("/exp-id/{expenseId}")
//    public ResponseEntity<Expense> retrieveExpense(@PathVariable Integer expenseId) {
//        Expense expense = expenseService.read(expenseId);
//
//        if (expense != null) {
//            return ResponseEntity.ok(expense);
//        }
//
//        return ResponseEntity.notFound().build();
//    }
    
    @PreAuthorize("@expenseService.isUserAccessingOwnExpenses(authentication,#expenseId) or (hasAnyAuthority('MANAGER') and @expenseService.isManagerAccessingOtherEmployees(authentication,#expenseId))") // TODO
    @GetMapping("/exp-id/{expenseId}")
    public ResponseEntity<Expense> retrieveExpense(@PathVariable Integer expenseId) {
        Expense expense = expenseService.read(expenseId);

        if (expense != null) {
            return ResponseEntity.ok(expense);
        }

        return ResponseEntity.notFound().build();
    }

    @PreAuthorize("hasAnyAuthority('EMPLOYEE','MANAGER') and @expenseService.isUserAccessingOwnExpenses(authentication,#expenseId)") // TODO
    @DeleteMapping("/delete/{expenseId}")
    public ResponseEntity<Expense> deleteExpense(@PathVariable Integer expenseId) {
        Expense expense = expenseService.delete(expenseId);

        if (expense != null) {
            return ResponseEntity.ok(expense);
        }

        return ResponseEntity.notFound().build();
    }

    @PreAuthorize("hasAuthority('MANAGER')")  //TODO
    @PutMapping("/add-comment/{expenseId}")
    public ResponseEntity<Expense> addMgrComment(@PathVariable Integer expenseId, @RequestParam("mgrComment") String managerComment) {
        Expense expense = expenseService.read(expenseId);

        if (expense != null) {
            expense.setMgrComment(managerComment);
            expenseRepository.save(expense);
            return ResponseEntity.ok(expense);
        }

        return ResponseEntity.notFound().build();
    }

    @PreAuthorize("hasAuthority('MANAGER') and @expenseService.isUserCreatingOwnExpense(authentication,#managerId)")
    @GetMapping("/manager/{managerId}/pending-expenses")
    public ResponseEntity<List<Expense>> retrievePendingExpensesByMgrId(@PathVariable Integer managerId) {
        List<Expense> expenses = expenseService.getPendingExpensesByMgrId(managerId);

        if (expenses == null) return ResponseEntity.notFound().build();

        if (expenses.size() > 0) {
            return ResponseEntity.ok(expenses);
        }

        return ResponseEntity.notFound().build();
    }

    @PreAuthorize("hasAuthority('MANAGER') and @expenseService.isUserCreatingOwnExpense(authentication,#managerId)")
    @GetMapping("/manager/{managerId}/approved-expenses")
    public ResponseEntity<List<Expense>> retrieveApprovedExpensesByMgrId(@PathVariable Integer managerId) {
        List<Expense> expenses = expenseService.getApprovedExpensesByMgrId(managerId);

        if (expenses == null) return ResponseEntity.notFound().build();

        if (expenses.size() > 0) {
            return ResponseEntity.ok(expenses);
        }

        return ResponseEntity.notFound().build();
    }

    @PreAuthorize("hasAuthority('MANAGER') and @expenseService.isUserCreatingOwnExpense(authentication,#managerId)")
    @GetMapping("/manager/{managerId}/rejected-expenses")
    public ResponseEntity<List<Expense>> retrieveRejectedExpensesByMgrId(@PathVariable Integer managerId) {
        List<Expense> expenses = expenseService.getRejectedExpensesByMgrId(managerId);

        if (expenses == null) return ResponseEntity.notFound().build();

        if (expenses.size() > 0) {
            return ResponseEntity.ok(expenses);
        }

        return ResponseEntity.notFound().build();
    }

//    @PreAuthorize("hasAuthority('MANAGER') and @expenseService.isManagerAccessingDashboard(authentication,#managerId)")
    //priority
    @PreAuthorize("hasAuthority('MANAGER') and @expenseService.isManagerAccessingDashboard(authentication, #managerId)")
    @GetMapping("/manager/{managerId}/all-expenses")
    public ResponseEntity<List<Expense>> retrieveAllExpensesByMgrId(@PathVariable Integer managerId) {
        List<Expense> expenses = expenseService.getAllExpensesByMgrId(managerId);

        if (expenses == null) return ResponseEntity.notFound().build();

        if (expenses.size() > 0) {
            return ResponseEntity.ok(expenses);
        }

        return ResponseEntity.notFound().build();
    }


    @PreAuthorize("hasAnyAuthority('EMPLOYEE', 'MANAGER')" )
    @PutMapping("/update-expense/{expenseId}")
    public ResponseEntity<Expense> updateExpenseDetails(@PathVariable Integer expenseId, @RequestParam("description") String description, @RequestParam("amount") Float amount) {
        Expense expense = expenseService.read(expenseId);


        if (expense != null) {
            expense.setDescription(description);
            expense.setAmount(amount);
            System.out.println("-----------111111111-----------------------------------");
            expenseRepository.save(expense);
            System.out.println("--------------------222222222222--------------------------");
            return ResponseEntity.ok(expense);
        }

        System.out.println("----------------------------------------------");

        return ResponseEntity.notFound().build();
    }
    
    


}