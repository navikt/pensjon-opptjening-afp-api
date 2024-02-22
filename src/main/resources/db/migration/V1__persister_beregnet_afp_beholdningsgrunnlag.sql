create table afp_beholdningsgrunnlag
(
    id uuid primary key not null,
    tidspunkt timestamptz not null,
    fnr varchar not null,
    uttaksdato timestamptz not null,
    konsument varchar not null,
    grunnlag jsonb not null
);
