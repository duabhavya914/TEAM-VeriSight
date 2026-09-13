create extension if not exists "pgcrypto";

create table public.protocols (
    id uuid primary key default gen_random_uuid(),

    test_id text not null unique,
    test_name text not null,
    drug_category text,
    target_substance text,
    test_type text,

    method text,
    reagents text,

    expected_observation text,
    colour_transition text,
    observation_location text,
    observation_time text,

    interpretation text,
    confirmation_status text,

    manual_section text,
    manual_page text,
    source text,
    application_scope text,

    officer_procedure text,
    field_app_eligibility text,
    camera_capture_note text,

    app_protocol_version text not null default '1.0',
    is_active boolean not null default true,

    created_at timestamptz not null default now(),
    updated_at timestamptz not null default now()
);

alter table public.protocols enable row level security;