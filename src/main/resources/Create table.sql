CREATE TABLE kurs_data(
	id_kurs INT AUTO_INCREMENT,
    id_bank INT not null,
    FOREIGN KEY(id_bank) REFERENCES bank_data (id_bank),
    currency varchar(20) not null,
    kurs_buy INT not null,
    kurs_bi INT not null,
    kurs_margin_buy INT not null,
    kurs_margin_sell INT not null,kmu dmn
    lma bet
    
    kurs_sell INT not null,
    created_date timestamp not null,
    last_update timestamp not null,
    PRIMARY KEY(id_kurs)
);

CREATE TABLE bank_data(
	id_bank INT AUTO_INCREMENT,
    bank_name varchar(20) not null,
    created_date timestamp not null,
    PRIMARY KEY(id_bank)
);
