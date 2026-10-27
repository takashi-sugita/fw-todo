-- Run once against an existing todoapp database before starting the updated app.
ALTER TABLE todos
    ADD COLUMN deleted_at DATETIME NULL;
