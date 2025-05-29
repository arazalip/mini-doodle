DROP INDEX IF EXISTS idx_time_slots_calendar_id;
DROP INDEX IF EXISTS idx_time_slots_start_time;
DROP INDEX IF EXISTS idx_time_slots_end_time;

CREATE INDEX idx_time_slots_calendar_status ON time_slots(calendar_id, status);

CREATE INDEX idx_time_slots_calendar_time_range ON time_slots(calendar_id, start_time, end_time);

CREATE INDEX idx_time_slots_user_time_status ON time_slots(calendar_id, status, start_time, end_time);

CREATE INDEX idx_time_slots_status ON time_slots(status);

CREATE INDEX idx_time_slots_calendar_id ON time_slots(calendar_id);