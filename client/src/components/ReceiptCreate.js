import React, { useState } from "react";
import axios from 'axios'
import { useParams } from 'react-router-dom'

const ReceiptCreate = () => {

    const { expenseId } = useParams()

    const [receiptData, setReceiptData] = useState({
        expenseId: expenseId,
        receiptFile: null,
        fileType: ''
    });

    const handleOnChange = (event) => {
        event.preventDefault()

        if (event.target.name === 'receiptFile' && event.target.files[0]){
            setReceiptData({
                ...receiptData,
                [event.target.name]: event.target.files[0]
            });
        }
        
    }

    const handleOnCreate = (event) => {
        event.preventDefault();
        const formData = new FormData();

        formData.append('expenseId', receiptData.expenseId);
        formData.append('file', receiptData.receiptFile);
        formData.append('fileType', 'pdf')
        
        axios
            .post(`http://localhost/expense/save/${expenseId}`, formData)
            .then((response) => {
                console.log("RECEIPT ID--->", response.data.id)
                //   window.location.href = `/add-receipt/${response.id}`
                // Handle successful response
            })
            .catch((error) => {
                console.log(error)
                // window.location.href = `/hr-reports`
            });
    }

    const handleOnSubmit = (event) => {
        event.preventDefault();

        axios
            .post('http://localhost/expense/submit', receiptData)
            .then((response) => {
                console.log("RECEIPT ID--->", response.id)
                alert("Expense Submitted Successfully!")
                //   window.location.href 
                // Handle successful response
            })
            .catch((error) => {
                console.log(error)
                // window.location.href = `/hr-reports`
            });

    }

    return (
        <div>
            <div class='lc-block my-5'>
                <h4>
                    Add the  <span className='mark'>Receipt</span>
                </h4>
            </div>
            <div class='text-start d-flex justify-content-center my-5'>
                <div class='w-50'>
                    <form class='d-grid gap-4'>

                        <div class='form-group row'>
                            <label for='receiptFile' class='col-sm-3 col-form-label'>
                                Receipt File
                            </label>
                            <div class='col-sm-9'>
                                <input
                                    type='file'
                                    name='receiptFile'
                                    class='form-control'
                                    id='receiptFile'
                                    onChange={handleOnChange}
                                />
                            </div>
                        </div>


                        <div class='form-group row'>
                            <div class='col-sm-10'>
                                <button
                                    type='submit'
                                    class='btn btn-primary '
                                    onClick={handleOnCreate}
                                >
                                    Add Receipt
                                </button>
                            </div>

                            <div class='form-group row'>
                                <div class='col-sm-10'>
                                    <button disabled
                                        type='submit'
                                        class='btn btn-primary '
                                        onClick={handleOnSubmit}
                                    >
                                        Submit Expense
                                    </button>
                                </div>


                            </div>
                        </div>

                    </form>
                </div>
            </div>
        </div>
    )
};

export default ReceiptCreate
