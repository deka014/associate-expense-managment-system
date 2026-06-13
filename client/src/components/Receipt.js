import React, { useEffect, useState } from 'react'
import axios from 'axios'
import { useParams } from 'react-router-dom'
import Loading from './Loading'

const Receipt = () => {
    const [receipt, setReceipt] = useState()
    const { expenseId } = useParams()
    const [isLoading, setIsLoading] = useState(true)

    useEffect(() => {
        // setIsLoading(true);
        async function fetchReceiptFile(expenseId) {
            try {
                const response = await axios.get(`http://localhost/expense/receipt/${expenseId}`)
                setReceipt(response.data)
                console.log("RECEIPT ---> ", response.data)
            }
            catch (error) {
                if (error.response && error.response.status === 403) {
                    window.location.href = '/'
                }

                else if (error.response && error.response.status === 401) {

                    window.location.href = '/login'
                } else {

                    console.error('Error fetching expenses data:', error)
                }
            }finally{
                setIsLoading(false)
            }
        }

        fetchReceiptFile(expenseId)
    }, [expenseId])

    if(isLoading)
    {
        return <div className="container ">
            <Loading />
        </div>
    }
    return (
        <div>
            {receipt ? (
                receipt.fileType === 'pdf' ? (
                    <iframe
                        src={`data:application/pdf;base64,${receipt.receiptFile}`}
                        width={600}
                        height={700}
                        frameBorder="0"
                    ></iframe>
                ) : (
                    <img
                        src={`data:application/jpg;base64,${receipt.receiptFile}`} // Ensure this is a valid URL or base64 string
                        width={600}
                        height={500}
                        alt="Receipt" // Add alt text for accessibility
                    />
                )
            ) : (
                <strong class="text-center fs-3" >File Not Found</strong>
            )}
        </div>
    );

}


export default Receipt
