package com.CRM.service.TransferTicket;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.CRM.request.PurchaseOrder.PurchaseOrderFilterRequest;
import com.CRM.request.TransferTicket.TransferTicketFilterRequest;
import com.CRM.request.TransferTicket.TransferTicketRequest;
import com.CRM.request.TransferTicket.ConfirmReceipt.ConfirmTransferTicketRequest;
import com.CRM.response.Pagination.APIResponse;
import com.CRM.response.Pagination.PagingResponse;
import com.CRM.response.PurchaseOrder.PurchaseOrderDetailResponse;
import com.CRM.response.PurchaseOrder.PurchaseOrderResponse;
import com.CRM.response.TransferTicket.BaseTransferTicketResponse;
import com.CRM.response.TransferTicket.TransferProductResponse;
import com.CRM.response.TransferTicket.TransferTicketDetailResponse;
import com.CRM.response.TransferTicket.ConfirmReceipt.ComfirmReceiptResponse;

public interface ITransferTicketService {
    PagingResponse<BaseTransferTicketResponse> getAllTransferTicket(int page, int limit, String sortBy, String direction, TransferTicketFilterRequest filter);

    APIResponse<Boolean> createTransferTicket(TransferTicketRequest transferTicketRequest);

    APIResponse<TransferTicketDetailResponse> getTransferTicketDetail(String id);

    APIResponse<Boolean> deleteTransferTicket(String id);

    void autoCleanTransferTicketTrash();

    PagingResponse<BaseTransferTicketResponse> getAllTransferTicketTrash(int page, int limit, String sortBy, String direction, TransferTicketFilterRequest filter);
    
    APIResponse<TransferTicketDetailResponse> getTransferTicketTrashDetail(String id);

    APIResponse<TransferTicketDetailResponse> confirmSendingTransferTicket(String ticketId);

    APIResponse<TransferTicketDetailResponse> getTransferTicketInfor(String ticketCode);

    List<TransferProductResponse> getProductsForNewTicket(String keyword);

    APIResponse<Boolean> markInTransit(String ticketCode);

    APIResponse<Boolean> confirmReceivedAtDestination(String ticketCode, ConfirmTransferTicketRequest request);

    APIResponse<ComfirmReceiptResponse> getInforConfirmReceipt(String ticketCode);
}
 