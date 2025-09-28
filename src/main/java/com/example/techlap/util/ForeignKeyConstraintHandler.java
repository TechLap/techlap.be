package com.example.techlap.util;

import com.example.techlap.exception.SQLForeignKeyException;
import org.springframework.dao.DataIntegrityViolationException;

/**
 * Utility class để xử lý foreign key constraint violations
 * Cung cấp các method tiện ích để xử lý lỗi ràng buộc khóa ngoại
 */
public class ForeignKeyConstraintHandler {
    
    /**
     * Xử lý delete operation với kiểm tra foreign key constraint
     * 
     * @param deleteOperation Lambda function thực hiện delete
     * @param entityName Tên entity đang xóa (VD: "sản phẩm", "danh mục")
     * @param relatedEntityName Tên entity liên quan (VD: "đơn hàng", "sản phẩm")
     * @throws SQLForeignKeyException Nếu có foreign key constraint violation
     */
    public static void handleDeleteWithForeignKeyCheck(Runnable deleteOperation, 
                                                      String entityName, 
                                                      String relatedEntityName) {
        try {
            deleteOperation.run();
        } catch (DataIntegrityViolationException e) {
            if (isForeignKeyConstraintViolation(e)) {
                throw new SQLForeignKeyException(
                    String.format("Không thể xóa %s này vì đã có %s liên quan. Vui lòng xóa các %s trước.", 
                        entityName, relatedEntityName, relatedEntityName)
                );
            }
            throw e;
        } catch (Exception e) {
            if (isForeignKeyConstraintViolation(e)) {
                throw new SQLForeignKeyException(
                    String.format("Không thể xóa %s này vì đã có %s liên quan. Vui lòng xóa các %s trước.", 
                        entityName, relatedEntityName, relatedEntityName)
                );
            }
            throw e;
        }
    }
    
    /**
     * Kiểm tra xem exception có phải là foreign key constraint violation không
     * 
     * @param e Exception cần kiểm tra
     * @return true nếu là foreign key constraint violation
     */
    private static boolean isForeignKeyConstraintViolation(Exception e) {
        if (e == null || e.getMessage() == null) {
            return false;
        }
        
        String message = e.getMessage().toLowerCase();
        return message.contains("foreign key constraint") || 
               message.contains("cannot delete or update a parent row");
    }
    
    /**
     * Tạo thông báo lỗi foreign key constraint
     * 
     * @param entityName Tên entity đang xóa
     * @param relatedEntityName Tên entity liên quan
     * @return Thông báo lỗi được format
     */
    public static String createForeignKeyErrorMessage(String entityName, String relatedEntityName) {
        return String.format("Không thể xóa %s này vì đã có %s liên quan. Vui lòng xóa các %s trước.", 
            entityName, relatedEntityName, relatedEntityName);
    }
}
