-- =====================
-- PROJECTS
-- =====================
INSERT INTO projects (name, project_key, issue_counter, description, created_at, updated_at) VALUES
('Development', 'DEV', 14, 'Core product development project', NOW(), NOW()),
('Mobile App',  'MOB', 6,  'iOS and Android mobile application', NOW(), NOW());


-- =====================
-- EPICS
-- =====================
INSERT INTO issues (title, issue_key, description, story_points, status, type, project_id, parent_id, due_date, created_at, updated_at) VALUES
('User Authentication', 'DEV-1', 'Handle all auth related features: login, registration, password reset', null, 'IN_PROGRESS', 'EPIC', 1, null, '2024-06-01 00:00:00', NOW(), NOW()),
('Payment Integration',  'DEV-2', 'Integrate Stripe for subscription billing', null, 'OPEN', 'EPIC', 1, null, '2024-07-01 00:00:00', NOW(), NOW()),
('Mobile Onboarding',   'MOB-1', 'First-time user onboarding flow', null, 'OPEN', 'EPIC', 2, null, '2024-08-01 00:00:00', NOW(), NOW());


-- =====================
-- STORIES & BUGFIXES (children of epics)
-- =====================
INSERT INTO issues (title, issue_key, description, story_points, status, type, project_id, parent_id, due_date, created_at, updated_at) VALUES
-- Under DEV-1 (Auth Epic)
('Implement JWT login',              'DEV-3',  'Create login endpoint with JWT token generation', 5, 'DONE',        'STORY',  1, 1, null, NOW(), NOW()),
('Registration flow',                'DEV-4',  'User registration with email verification',       8, 'IN_PROGRESS', 'STORY',  1, 1, null, NOW(), NOW()),
('Password reset via email',         'DEV-5',  'Forgot password flow using email link',           3, 'OPEN',        'STORY',  1, 1, null, NOW(), NOW()),
('Login fails for OAuth users',      'DEV-6',  'Users registered via Google cannot log in with password', 2, 'OPEN', 'BUGFIX', 1, 1, null, NOW(), NOW()),

-- Under DEV-2 (Payment Epic)
('Stripe checkout integration',      'DEV-7',  'Embed Stripe checkout for plan upgrades',         8, 'IN_PROGRESS', 'STORY',  1, 2, null, NOW(), NOW()),
('Webhook handler for Stripe events','DEV-8',  'Handle payment_intent.succeeded and failures',    5, 'OPEN',        'STORY',  1, 2, null, NOW(), NOW()),
('Duplicate charge on retry',        'DEV-9',  'Retrying failed payment causes double charge',    3, 'IN_PROGRESS', 'BUGFIX', 1, 2, null, NOW(), NOW()),

-- Under MOB-1 (Onboarding Epic)
('Splash screen and intro slides',   'MOB-2',  'Design and implement onboarding carousel',        3, 'DONE',        'STORY',  2, 3, null, NOW(), NOW()),
('Permissions request flow',         'MOB-3',  'Ask for notifications and location permissions',  2, 'OPEN',        'STORY',  2, 3, null, NOW(), NOW());


-- =====================
-- SUBTASKS (children of stories/bugfixes)
-- =====================
INSERT INTO issues (title, issue_key, description, story_points, status, type, project_id, parent_id, due_date, created_at, updated_at) VALUES
-- Under DEV-3 (JWT login story)
('Create /auth/login endpoint',      'DEV-10', null, 2, 'DONE', 'SUBTASK', 1, 4,  null, NOW(), NOW()),
('Write JWT utility class',          'DEV-11', null, 1, 'DONE', 'SUBTASK', 1, 4,  null, NOW(), NOW()),

-- Under DEV-4 (Registration story)
('Create /auth/register endpoint',   'DEV-12', null, 3, 'DONE',        'SUBTASK', 1, 5, null, NOW(), NOW()),
('Send verification email',          'DEV-13', null, 2, 'IN_PROGRESS', 'SUBTASK', 1, 5, null, NOW(), NOW()),

-- Under DEV-7 (Stripe checkout)
('Add Stripe SDK dependency',        'DEV-14', null, 1, 'DONE',        'SUBTASK', 1, 8, null, NOW(), NOW()),

-- Under MOB-2 (Splash screen)
('MOB-5', 'MOB-5', null, 1, 'DONE', 'SUBTASK', 2, 11, null, NOW(), NOW()),
('MOB-6', 'MOB-6', null, 1, 'DONE', 'SUBTASK', 2, 11, null, NOW(), NOW());