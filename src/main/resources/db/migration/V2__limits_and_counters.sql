create table if not exists limit_rule (
                                          id uuid primary key,
                                          scope_type varchar(8) not null,
                                          scope_key varchar(128) not null,
                                          currency varchar(3) not null,
                                          credit_limit_cents bigint not null,
                                          daily_limit_cents bigint not null default 0,
                                          monthly_limit_cents bigint not null default 0,
                                          created_at timestamptz not null,
                                          updated_at timestamptz not null,
                                          version bigint not null default 0,
                                          unique(scope_type, scope_key)
);

create table if not exists spend_counter (
                                             scope_type varchar(8) not null,
                                             scope_key varchar(128) not null,
                                             period_type varchar(8) not null,
                                             period_start timestamptz not null,
                                             currency varchar(3) not null,
                                             amount_cents bigint not null,
                                             updated_at timestamptz not null,
                                             primary key (scope_type, scope_key, period_type, period_start)
);