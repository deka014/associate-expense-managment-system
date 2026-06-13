import React, { useState } from "react";
import axios from 'axios'
import { useParams } from 'react-router-dom'

const ExpenseSubmit = () => {

    const { expenseId } = useParams()

    const handleOnSubmit = () => {
         axios
            .put(`http://localhost/expense/submit/${expenseId}`)
            .then((response) => {
                console.log("EXPENSE ID--->", expenseId)
                alert("Expense Submitted Successfully!")
                // window.location.href = `/submit-expense/${expenseId}`
                // Handle successful response
            })
            .catch((error) => {
                console.log(error)
                // window.location.href = `/hr-reports`
            });
    }

    handleOnSubmit();
}

export default ExpenseSubmit
