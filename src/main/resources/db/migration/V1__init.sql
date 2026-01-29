create table if not exists ledger_account (
                                              id uuid primary key,
                                              credit_limit_cents bigint not null,
                                              currency varchar(3) not null,
                                              created_at timestamptz not null,
                                              version bigint not null default 0
);

create table if not exists ledger_entry (
                                            id uuid primary key,
                                            account_id uuid not null references ledger_account(id),
                                            payment_id uuid,
                                            entry_type varchar(32) not null,
                                            direction varchar(8) not null,
                                            amount_cents bigint not null,
                                            currency varchar(3) not null,
                                            occurred_at timestamptz not null,
                                            correlation_id varchar(128) not null
);

create index if not exists idx_ledger_entry_account on ledger_entry(account_id, occurred_at);
create index if not exists idx_ledger_entry_payment on ledger_entry(payment_id);

create table if not exists outbox_event (
                                            id uuid primary key,
                                            aggregate_type varchar(64) not null,
                                            aggregate_id uuid not null,
                                            topic varchar(128) not null,
                                            message_key varchar(128) not null,
                                            payload_json text not null,
                                            status varchar(16) not null,
                                            attempts int not null,
                                            created_at timestamptz not null,
                                            sent_at timestamptz,
                                            version bigint not null default 0
);

create index if not exists idx_outbox_status on outbox_event(status, created_at);