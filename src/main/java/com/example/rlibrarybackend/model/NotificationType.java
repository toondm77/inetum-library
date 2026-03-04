package com.example.rlibrarybackend.model;

public enum NotificationType {
    LOAN_DUE_SOON,       // Boek moet binnenkort teruggebracht worden
    LOAN_OVERDUE,        // Boek is te laat teruggegeven
    LOAN_RETURNED,       // Boek succesvol teruggebracht
    NEW_BOOK_AVAILABLE,  // Nieuw boek beschikbaar in favoriete thema
    ACCOUNT_UPDATED,     // Account gewijzigd
    COMPLAINT_STATUS,    // Update over ingediende klacht
    SYSTEM              // Algemene systeemmelding
}

