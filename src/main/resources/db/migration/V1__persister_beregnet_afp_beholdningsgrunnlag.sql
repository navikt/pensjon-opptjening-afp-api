create table afp_beholdningsgrunnlag
(
    id uuid primary key not null,
    tidspunkt timestamptz not null,
    fnr varchar not null,
    uttaksdato date not null,
    konsument varchar not null,
    grunnlag jsonb not null
);
