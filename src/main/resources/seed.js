// ============================================================
//  MongoDB Seed Script
//  Run with: mongosh <your-db-name> seed.js
//  Or:       mongosh "mongodb://localhost:27017/<your-db-name>" seed.js
// ============================================================

// --- Helpers -------------------------------------------------

function isoDate(offsetDays = 0) {
    const d = new Date();
    d.setDate(d.getDate() + offsetDays);
    return d;
}

// Spring Boot stores LocalDateTime as an array [year,month,day,hour,min,sec,nano]
// when using the default Jackson JavaTimeModule — but many projects configure it
// to store as ISODate. This script uses ISODate (the sane default with Spring Data MongoDB).
// If your app stores dates as arrays, replace isoDate() calls with the array form.

// --- Clean slate ---------------------------------------------

db.users.drop();
db.projects.drop();
db.issues.drop();

// =============================================================
//  USERS
// =============================================================
//  Passwords below are BCrypt hashes of "password" — replace
//  with your own hashed values before using in production.
//  BCrypt of "password":
//  $2a$10$hX06PoLxl6CW7R5x/83J4OH9GPKX/sU2lbymR05ZLQcjgi8zF4ICO

const BCRYPT_PASSWORD = "$2a$10$hX06PoLxl6CW7R5x/83J4OH9GPKX/sU2lbymR05ZLQcjgi8zF4ICO";

// ---- Permissions ---------------------------------------------

const projectCreate = { id: "p-project-c",      name: "PROJECT_CREATE" };
const projectDelete = { id: "p-project-d",      name: "PROJECT_DELETE" };
const issueCreate   = { id: "p-issue-c",        name: "ISSUE_CREATE" };
const issueDelete   = { id: "p-issue-d",        name: "ISSUE_DELETE" };
const userManage    = { id: "p-user-manage",    name: "USER_MANAGE" };

// ---- Users ---------------------------------------------------

db.users.insertMany([
    {
        _id:              "user-alice",
        email:            "alice@example.com",
        username:         "alice",
        password:         BCRYPT_PASSWORD,
        registrationDate: isoDate(-120),
        permissions:      [projectCreate, projectDelete, issueCreate, issueDelete, userManage]
    },
    {
        _id:              "user-bob",
        email:            "bob@example.com",
        username:         "bob",
        password:         BCRYPT_PASSWORD,
        registrationDate: isoDate(-90),
        permissions:      [issueCreate, issueDelete, projectCreate]
    },
    {
        _id:              "user-carol",
        email:            "carol@example.com",
        username:         "carol",
        password:         BCRYPT_PASSWORD,
        registrationDate: isoDate(-60),
        permissions:      [projectCreate, projectDelete, issueCreate, issueDelete, userManage]
    },
    {
        _id:              "user-dave",
        email:            "dave@example.com",
        username:         "dave",
        password:         BCRYPT_PASSWORD,
        registrationDate: isoDate(-45),
        permissions:      [issueCreate]
    },
    {
        _id:              "user-eve",
        email:            "eve@example.com",
        username:         "eve",
        password:         BCRYPT_PASSWORD,
        registrationDate: isoDate(-30),
        permissions:      [issueCreate]
    },
    {
        _id:              "user-frank",
        email:            "frank@example.com",
        username:         "frank",
        password:         BCRYPT_PASSWORD,
        registrationDate: isoDate(-20),
        permissions:      [issueCreate, issueDelete, projectCreate]
    }
]);

print("✔  Inserted " + db.users.countDocuments() + " users");

// =============================================================
//  PROJECTS
// =============================================================

// ---- Statuses -----------------------------------------------

const statusTodo          = { id: "s-todo",        name: "To Do",       category: "TODO",        color: "#6B778C", displayOrder: 1, isDefault: true,  allowedTransitionIds: ["s-inprogress"] };
const statusInProgress    = { id: "s-inprogress",  name: "In Progress", category: "IN_PROGRESS", color: "#0052CC", displayOrder: 2, isDefault: false, allowedTransitionIds: ["s-review", "s-todo"] };
const statusReview        = { id: "s-review",      name: "In Review",   category: "IN_PROGRESS", color: "#FF991F", displayOrder: 3, isDefault: false, allowedTransitionIds: ["s-inprogress", "s-done"] };
const statusDone          = { id: "s-done",        name: "Done",        category: "DONE",        color: "#36B37E", displayOrder: 4, isDefault: false, allowedTransitionIds: [] };

// Second project statuses
const statusBacklog       = { id: "b-backlog",     name: "Backlog",     category: "TODO",        color: "#6B778C", displayOrder: 1, isDefault: true,  allowedTransitionIds: ["b-dev"] };
const statusDev           = { id: "b-dev",         name: "Development", category: "IN_PROGRESS", color: "#0052CC", displayOrder: 2, isDefault: false, allowedTransitionIds: ["b-qa", "b-backlog"] };
const statusQA            = { id: "b-qa",          name: "QA",          category: "IN_PROGRESS", color: "#FF991F", displayOrder: 3, isDefault: false, allowedTransitionIds: ["b-dev", "b-released"] };
const statusReleased      = { id: "b-released",    name: "Released",    category: "DONE",        color: "#36B37E", displayOrder: 4, isDefault: false, allowedTransitionIds: [] };

// ---- Project documents --------------------------------------

const projectAlpha = {
    _id:          "proj-alpha-001",
    key:          "ALPHA",
    name:         "Project Alpha",
    issueCounter: 12,
    description:  "Main product development project.",
    statuses:     [statusTodo, statusInProgress, statusReview, statusDone],
    createdAt:    isoDate(-90),
    updatedAt:    isoDate(-1),
    createdBy:    "user-alice",
    modifiedBy:   "user-bob",
    visibility:   "PUBLIC"
};

const projectBeta = {
    _id:          "proj-beta-002",
    key:          "BETA",
    name:         "Project Beta",
    issueCounter: 7,
    description:  "Internal tooling and infrastructure improvements.",
    statuses:     [statusBacklog, statusDev, statusQA, statusReleased],
    createdAt:    isoDate(-45),
    updatedAt:    isoDate(-3),
    createdBy:    "user-carol",
    modifiedBy:   "user-carol",
    visibility:   "PRIVATE"
};

db.projects.insertMany([projectAlpha, projectBeta]);
print("✔  Inserted " + db.projects.countDocuments() + " projects");

// =============================================================
//  ISSUES  –  Project Alpha
// =============================================================

// ---- EPIC ---------------------------------------------------

const epicAuth = {
    _id:         "issue-alpha-001",
    key:         "ALPHA-1",
    title:       "Authentication & Authorisation",
    description: "All work related to user auth, OAuth, JWT, and RBAC.",
    storyPoints: 40,
    dueDate:     isoDate(30),
    statusId:    "s-inprogress",
    projectId:   "proj-alpha-001",
    type:        "EPIC",
    parentId:    null,
    links:       [],
    createdAt:   isoDate(-80),
    updatedAt:   isoDate(-10),
    createdBy:   "user-alice",
    modifiedBy:  "user-alice",
    assignee:    "user-bob",
    reporter:    "user-alice",
    priority:    "HIGH"
};

const epicDashboard = {
    _id:         "issue-alpha-002",
    key:         "ALPHA-2",
    title:       "Analytics Dashboard",
    description: "Build the main analytics and reporting dashboard.",
    storyPoints: 55,
    dueDate:     isoDate(60),
    statusId:    "s-todo",
    projectId:   "proj-alpha-001",
    type:        "EPIC",
    parentId:    null,
    links:       [],
    createdAt:   isoDate(-70),
    updatedAt:   isoDate(-5),
    createdBy:   "user-alice",
    modifiedBy:  "user-alice",
    assignee:    null,
    reporter:    "user-alice",
    priority:    "MEDIUM"
};

// ---- STORIES under epicAuth ---------------------------------

const storyLogin = {
    _id:         "issue-alpha-003",
    key:         "ALPHA-3",
    title:       "User Login Flow",
    description: "Implement email/password login with JWT response.",
    storyPoints: 5,
    dueDate:     isoDate(10),
    statusId:    "s-done",
    projectId:   "proj-alpha-001",
    type:        "STORY",
    parentId:    "issue-alpha-001",
    links: [
        {
            targetIssueId: "issue-alpha-004",
            linkType:      "BLOCKS",
            createdAt:     isoDate(-50),
            createdBy:     "user-alice"
        }
    ],
    createdAt:   isoDate(-60),
    updatedAt:   isoDate(-2),
    createdBy:   "user-alice",
    modifiedBy:  "user-bob",
    assignee:    "user-bob",
    reporter:    "user-alice",
    priority:    "HIGH"
};

const storyOAuth = {
    _id:         "issue-alpha-004",
    key:         "ALPHA-4",
    title:       "OAuth2 Social Login",
    description: "Add Google and GitHub OAuth2 social login options.",
    storyPoints: 8,
    dueDate:     isoDate(15),
    statusId:    "s-inprogress",
    projectId:   "proj-alpha-001",
    type:        "STORY",
    parentId:    "issue-alpha-001",
    links: [
        {
            targetIssueId: "issue-alpha-003",
            linkType:      "BLOCKED_BY",
            createdAt:     isoDate(-50),
            createdBy:     "user-alice"
        }
    ],
    createdAt:   isoDate(-55),
    updatedAt:   isoDate(-1),
    createdBy:   "user-alice",
    modifiedBy:  "user-bob",
    assignee:    "user-bob",
    reporter:    "user-alice",
    priority:    "HIGH"
};

const storyRBAC = {
    _id:         "issue-alpha-005",
    key:         "ALPHA-5",
    title:       "Role-Based Access Control",
    description: "Define ADMIN, MEMBER, VIEWER roles and enforce them on all endpoints.",
    storyPoints: 13,
    dueDate:     isoDate(20),
    statusId:    "s-todo",
    projectId:   "proj-alpha-001",
    type:        "STORY",
    parentId:    "issue-alpha-001",
    links: [],
    createdAt:   isoDate(-50),
    updatedAt:   isoDate(-50),
    createdBy:   "user-alice",
    modifiedBy:  "user-alice",
    assignee:    "user-carol",
    reporter:    "user-alice",
    priority:    "MEDIUM"
};

// ---- TASK & SUBTASKS ----------------------------------------

const taskSetupJWT = {
    _id:         "issue-alpha-006",
    key:         "ALPHA-6",
    title:       "Configure JWT signing keys",
    description: "Generate RSA key pair and store in Vault; wire up Spring Security filter.",
    storyPoints: 3,
    dueDate:     isoDate(5),
    statusId:    "s-done",
    projectId:   "proj-alpha-001",
    type:        "TASK",
    parentId:    "issue-alpha-003",
    links:       [],
    createdAt:   isoDate(-45),
    updatedAt:   isoDate(-20),
    createdBy:   "user-bob",
    modifiedBy:  "user-bob",
    assignee:    "user-bob",
    reporter:    "user-bob",
    priority:    "HIGH"
};

const subtaskWriteTests = {
    _id:         "issue-alpha-007",
    key:         "ALPHA-7",
    title:       "Write integration tests for /auth/login",
    description: "Cover happy path, bad credentials, locked account, and rate-limiting scenarios.",
    storyPoints: 2,
    dueDate:     isoDate(8),
    statusId:    "s-review",
    projectId:   "proj-alpha-001",
    type:        "SUBTASK",
    parentId:    "issue-alpha-006",
    links:       [],
    createdAt:   isoDate(-30),
    updatedAt:   isoDate(-3),
    createdBy:   "user-bob",
    modifiedBy:  "user-carol",
    assignee:    "user-carol",
    reporter:    "user-bob",
    priority:    "MEDIUM"
};

// ---- BUGFIX -------------------------------------------------

const bugTokenExpiry = {
    _id:         "issue-alpha-008",
    key:         "ALPHA-8",
    title:       "JWT expiry not enforced on /api/profile",
    description: "Expired tokens are still accepted by the profile endpoint due to missing filter ordering.",
    storyPoints: 2,
    dueDate:     isoDate(2),
    statusId:    "s-inprogress",
    projectId:   "proj-alpha-001",
    type:        "BUGFIX",
    parentId:    null,
    links: [
        {
            targetIssueId: "issue-alpha-009",
            linkType:      "DUPLICATES",
            createdAt:     isoDate(-5),
            createdBy:     "user-dave"
        }
    ],
    createdAt:   isoDate(-7),
    updatedAt:   isoDate(-1),
    createdBy:   "user-dave",
    modifiedBy:  "user-bob",
    assignee:    "user-bob",
    reporter:    "user-dave",
    priority:    "HIGHEST"
};

const bugTokenExpiry2 = {
    _id:         "issue-alpha-009",
    key:         "ALPHA-9",
    title:       "[Duplicate] Token validation skipped on profile route",
    description: "Same root cause as ALPHA-8, raised independently.",
    storyPoints: null,
    dueDate:     null,
    statusId:    "s-done",
    projectId:   "proj-alpha-001",
    type:        "BUGFIX",
    parentId:    null,
    links: [
        {
            targetIssueId: "issue-alpha-008",
            linkType:      "DUPLICATED_BY",
            createdAt:     isoDate(-5),
            createdBy:     "user-dave"
        }
    ],
    createdAt:   isoDate(-6),
    updatedAt:   isoDate(-5),
    createdBy:   "user-eve",
    modifiedBy:  "user-alice",
    assignee:    null,
    reporter:    "user-eve",
    priority:    "HIGH"
};

// =============================================================
//  ISSUES  –  Project Beta
// =============================================================

const epicCICD = {
    _id:         "issue-beta-001",
    key:         "BETA-1",
    title:       "CI/CD Pipeline Overhaul",
    description: "Migrate from Jenkins to GitHub Actions; add SAST and container scanning.",
    storyPoints: 34,
    dueDate:     isoDate(45),
    statusId:    "b-dev",
    projectId:   "proj-beta-002",
    type:        "EPIC",
    parentId:    null,
    links:       [],
    createdAt:   isoDate(-40),
    updatedAt:   isoDate(-4),
    createdBy:   "user-carol",
    modifiedBy:  "user-carol",
    assignee:    "user-frank",
    reporter:    "user-carol",
    priority:    "HIGH"
};

const storyGHActions = {
    _id:         "issue-beta-002",
    key:         "BETA-2",
    title:       "Set up GitHub Actions workflows",
    description: "Create build, test, and deploy workflows for all microservices.",
    storyPoints: 8,
    dueDate:     isoDate(20),
    statusId:    "b-dev",
    projectId:   "proj-beta-002",
    type:        "STORY",
    parentId:    "issue-beta-001",
    links: [
        {
            targetIssueId: "issue-beta-003",
            linkType:      "RELATES_TO",
            createdAt:     isoDate(-20),
            createdBy:     "user-carol"
        }
    ],
    createdAt:   isoDate(-35),
    updatedAt:   isoDate(-4),
    createdBy:   "user-carol",
    modifiedBy:  "user-frank",
    assignee:    "user-frank",
    reporter:    "user-carol",
    priority:    "HIGH"
};

const storySAST = {
    _id:         "issue-beta-003",
    key:         "BETA-3",
    title:       "Integrate SAST scanning (CodeQL)",
    description: "Add CodeQL analysis step to PR workflow; block merges on HIGH findings.",
    storyPoints: 5,
    dueDate:     isoDate(25),
    statusId:    "b-backlog",
    projectId:   "proj-beta-002",
    type:        "STORY",
    parentId:    "issue-beta-001",
    links: [
        {
            targetIssueId: "issue-beta-002",
            linkType:      "RELATES_TO",
            createdAt:     isoDate(-20),
            createdBy:     "user-carol"
        }
    ],
    createdAt:   isoDate(-30),
    updatedAt:   isoDate(-30),
    createdBy:   "user-carol",
    modifiedBy:  "user-carol",
    assignee:    null,
    reporter:    "user-carol",
    priority:    "MEDIUM"
};

const bugPipelineFlaky = {
    _id:         "issue-beta-004",
    key:         "BETA-4",
    title:       "Integration tests flaky in CI – random port conflicts",
    description: "Tests pass locally but ~20 % of CI runs fail with 'address already in use'.",
    storyPoints: 3,
    dueDate:     isoDate(7),
    statusId:    "b-qa",
    projectId:   "proj-beta-002",
    type:        "BUGFIX",
    parentId:    null,
    links:       [],
    createdAt:   isoDate(-10),
    updatedAt:   isoDate(-2),
    createdBy:   "user-frank",
    modifiedBy:  "user-frank",
    assignee:    "user-frank",
    reporter:    "user-frank",
    priority:    "HIGH"
};

db.issues.insertMany([
    epicAuth, epicDashboard,
    storyLogin, storyOAuth, storyRBAC,
    taskSetupJWT, subtaskWriteTests,
    bugTokenExpiry, bugTokenExpiry2,
    epicCICD, storyGHActions, storySAST, bugPipelineFlaky
]);

print("✔  Inserted " + db.issues.countDocuments() + " issues");

// =============================================================
//  Sanity checks
// =============================================================

print("\n--- Project summary ---");
db.projects.find({}, { key: 1, name: 1, issueCounter: 1 }).forEach(p =>
    print("  " + p.key + "  " + p.name + "  (counter=" + p.issueCounter + ")")
);

print("\n--- Issues by type ---");
["EPIC","STORY","TASK","SUBTASK","BUGFIX"].forEach(t =>
    print("  " + t + ": " + db.issues.countDocuments({ type: t }))
);

print("\n--- Issues by status ---");
db.issues.aggregate([
    { $group: { _id: "$statusId", count: { $sum: 1 } } },
    { $sort:  { _id: 1 } }
]).forEach(r => print("  " + r._id + ": " + r.count));

print("\nSeed complete ✔");